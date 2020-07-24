package org.vitrivr.cineast.core.db.cottontaildb;

import static org.vitrivr.cineast.core.db.cottontaildb.CottontailMessageBuilder.CINEAST_SCHEMA;
import static org.vitrivr.cineast.core.db.cottontaildb.CottontailMessageBuilder.whereInList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig.Distance;
import org.vitrivr.cineast.core.data.distance.DistanceElement;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.db.DBSelector;
import org.vitrivr.cineast.core.db.MergeOperation;
import org.vitrivr.cineast.core.db.RelationalOperator;
import org.vitrivr.cottontail.grpc.CottontailGrpc.CompoundBooleanPredicate.Operator;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Data;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Entity;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Knn;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Projection;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Projection.Operation;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Query;
import org.vitrivr.cottontail.grpc.CottontailGrpc.QueryResultMessage;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Tuple;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Where;

public class CottontailSelector implements DBSelector {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Projection SELECT_ALL_PROJECTION = CottontailMessageBuilder.projection(Operation.SELECT, "*");

  private final CottontailWrapper cottontail;
  private Entity entity;

  public CottontailSelector(CottontailWrapper wrapper) {
    this.cottontail = wrapper;
  }

  @Override
  public boolean open(String name) {
    this.entity = CottontailMessageBuilder.entity(name);
    return true;
  }

  @Override
  public boolean close() {
    //this.cottontail.close();
    return true;
  }

  /**
   * if {@link ReadableQueryConfig#getRelevantSegmentIds()} is null, the where-clause will be left empty
   */
  @Override
  public <E extends DistanceElement> List<E> getNearestNeighboursGeneric(int k, float[] vector, String column, Class<E> distanceElementClass, ReadableQueryConfig config) {

    Knn knn = CottontailMessageBuilder.knn(column, vector, config.getDistanceWeights().orElse(null), k, config.getDistance().orElse(Distance.manhattan));

    List<QueryResultMessage> results = this.cottontail.query(
        CottontailMessageBuilder.queryMessage(
            CottontailMessageBuilder.query(entity,
                CottontailMessageBuilder.projection(Operation.SELECT, "id", "distance"), whereInList("id", config.getRelevantSegmentIds()), knn, k),
            config.getQueryId().toString()));

    return new ArrayList<>(handleNearestNeighbourResponse(results, distanceElementClass));
  }

  @Override
  public <E extends DistanceElement> List<E> getBatchedNearestNeighbours(int k, List<float[]> vectors, String column, Class<E> distanceElementClass, List<ReadableQueryConfig> configs) {

    Query query = CottontailMessageBuilder.query(
        entity,
        CottontailMessageBuilder.projection(Operation.SELECT, "id", "distance"),
        whereInList("id", configs.get(0).getRelevantSegmentIds()),
        CottontailMessageBuilder.batchedKnn(
            column,
            vectors,
            null,
            k,
            configs.get(0).getDistance().orElse(Distance.manhattan)), null);

    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(query, configs.get(0).getQueryId().toString()));

    return new ArrayList<>(handleNearestNeighbourResponse(results, distanceElementClass));
  }

  @Override
  public <T extends DistanceElement> List<T> getCombinedNearestNeighbours(int k, List<float[]> vectors, String column, Class<T> distanceElementClass, List<ReadableQueryConfig> configs, MergeOperation merge, Map<String, String> options) { // TODO
    return null;
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getNearestNeighbourRows(int k, float[] vector, String column, ReadableQueryConfig config) {

    Knn knn =
        CottontailMessageBuilder.knn(
            column,
            vector,
            config.getDistanceWeights().orElse(null),
            k,
            config.getDistance().orElse(Distance.manhattan));

    List<QueryResultMessage> results =
        this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, SELECT_ALL_PROJECTION, whereInList("id", config.getRelevantSegmentIds()), knn, k), config.getQueryId().toString()));

    return processResults(results);
  }

  @Override
  public List<float[]> getFeatureVectors(String fieldName, PrimitiveTypeProvider value, String vectorName) {

    Projection projection = CottontailMessageBuilder.projection(Operation.SELECT, vectorName);
    Where where = CottontailMessageBuilder.atomicWhere(fieldName, RelationalOperator.EQ, CottontailMessageBuilder.toData(value));

    List<QueryResultMessage> results =
        this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, projection, where, null, null), null));

    return results.stream().map(el -> {
      List<Float> list = el.getTuple().getDataOrThrow(vectorName).getVectorData().getFloatVector().getVectorList();
      float[] arr = new float[list.size()];
      int i = 0;
      for (Float f : list) {
        arr[i++] = f;
      }
      return arr;
    }).collect(Collectors.toList());
  }

  @Override
  public List<PrimitiveTypeProvider> getFeatureVectorsGeneric(String fieldName, PrimitiveTypeProvider value, String vectorName) {

    Projection projection = CottontailMessageBuilder.projection(Operation.SELECT, vectorName);
    Where where = CottontailMessageBuilder.atomicWhere(fieldName, RelationalOperator.EQ, CottontailMessageBuilder.toData(value));

    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, projection, where, null, null), null));

    return results.stream().map(el -> CottontailMessageBuilder.fromData(el.getTuple().getDataMap().get(vectorName))).collect(Collectors.toList());
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, Iterable<PrimitiveTypeProvider> values) {
    return getRows(fieldName, CottontailMessageBuilder.toDatas(values));
  }

  public List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, Data... values) {
    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, SELECT_ALL_PROJECTION, CottontailMessageBuilder.atomicWhere(fieldName, RelationalOperator.IN, values), null, null), null));
    return processResults(results);
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, List<String> values) {
    return getRows(fieldName, CottontailMessageBuilder.toDatas(values));
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getFulltextRows(
      int rows, String fieldname, ReadableQueryConfig queryConfig, String... terms) {

    /* This includes the filter for segmentids in the where-statement */
    Where where = CottontailMessageBuilder.compoundWhere(queryConfig, fieldname, RelationalOperator.LIKE, Operator.OR, CottontailMessageBuilder.toDatas(Arrays.asList(terms)));

    final Projection projection = Projection.newBuilder().setOp(Operation.SELECT).putAttributes("id", "").putAttributes("score", "ap_score").build();

    final List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, projection, where, null, rows), null));

    return processResults(results);
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, RelationalOperator operator, Iterable<PrimitiveTypeProvider> values) {

    Where where = CottontailMessageBuilder.atomicWhere(fieldName, operator, CottontailMessageBuilder.toDatas(values));

    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, SELECT_ALL_PROJECTION, where, null, null), null));

    return processResults(results);
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getRowsAND(List<Triple<String, RelationalOperator, List<PrimitiveTypeProvider>>> conditions, String identifier, List<String> projection) {
    Where where = CottontailMessageBuilder.compoundWhere(conditions);

    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, CottontailMessageBuilder.projection(Operation.SELECT, projection.toArray(new String[]{})), where, null, null), null));

    return processResults(results);
  }

  @Override
  public List<PrimitiveTypeProvider> getAll(String column) {

    Projection projection = CottontailMessageBuilder.projection(Operation.SELECT, column);

    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, projection, null, null, null), null));

    return results.stream().map(el -> CottontailMessageBuilder.fromData(el.getTuple().getDataMap().get(column))).collect(Collectors.toList());
  }

  @Override
  public List<PrimitiveTypeProvider> getUniqueValues(String column) {
    List<PrimitiveTypeProvider> _return = new ArrayList<>();
    List<QueryResultMessage> results = this.cottontail.query(CottontailMessageBuilder.queryMessage(
        CottontailMessageBuilder.query(entity, CottontailMessageBuilder.projection(Operation.SELECT_DISTINCT, column), null, null, null), null));

    results.forEach(tuple -> _return.add(CottontailMessageBuilder.fromData(tuple.getTuple().getDataOrThrow(column))));
    return _return;
  }

  @Override
  public List<Map<String, PrimitiveTypeProvider>> getAll() {
    List<QueryResultMessage> results =
        this.cottontail.query(
            CottontailMessageBuilder.queryMessage(
                CottontailMessageBuilder.query(entity, SELECT_ALL_PROJECTION, null, null, null), null));

    return processResults(results);
  }

  @Override
  public boolean existsEntity(String name) {

    List<Entity> entities = this.cottontail.listEntities(CINEAST_SCHEMA);

    for (Entity entity : entities) {
      if (entity.getName().equals(name)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean ping() {
    return this.cottontail.ping();
  }

  private static List<Map<String, PrimitiveTypeProvider>> processResults(
      List<QueryResultMessage> queryResponses) {
    StopWatch watch = StopWatch.createStarted();

    List<Map<String, PrimitiveTypeProvider>> _return = queryResponses.stream().map(response -> CottontailMessageBuilder.tupleToMap(response.getTuple())).collect(Collectors.toList());

    LOGGER.trace("Processed {} results in {} ms", _return.size(), watch.getTime(TimeUnit.MILLISECONDS));

    return _return;
  }

  private static <T extends DistanceElement> List<T> handleNearestNeighbourResponse(
      List<QueryResultMessage> response, Class<? extends T> distanceElementClass) {
    List<T> result = new ArrayList<>();
    for (QueryResultMessage msg : response) {
      Tuple t = msg.getTuple();
      String id = null;
      Data data = t.getDataMap().get("id");
      switch (t.getDataMap().get("id").getDataCase()) {
        case BOOLEANDATA:
        case VECTORDATA:
        case DATA_NOT_SET:
        case NULLDATA:
          continue;
        case INTDATA:
          id = String.valueOf(data.getIntData());
          break;
        case LONGDATA:
          id = String.valueOf(data.getLongData());
          break;
        case FLOATDATA:
          id = String.valueOf(data.getFloatData());
          break;
        case DOUBLEDATA:
          id = String.valueOf(data.getDoubleData());
          break;
        case STRINGDATA:
          id = data.getStringData();
          break;
      }
      if (id == null) {
        continue;
      }
      double distance =
          t.getDataMap().get("distance").getDoubleData(); // TODO what key is used for the distance?
      T e = DistanceElement.create(distanceElementClass, id, distance);
      result.add(e);
    }

    return result;
  }
}
