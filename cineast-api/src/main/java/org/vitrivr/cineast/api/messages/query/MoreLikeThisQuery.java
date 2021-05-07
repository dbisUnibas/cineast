package org.vitrivr.cineast.api.messages.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.vitrivr.cineast.api.messages.interfaces.MessageType;
import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;

/**
 * This object represents a MoreLikeThisQuery message, i.e. a request for more items similar to a
 * given item.
 *
 * @author rgasser
 * @created 27.04.17
 */
public class MoreLikeThisQuery extends Query {

  /**
   * ID of the segment that serves as example for the more like this query.
   */
  private String segmentId;

  /**
   * List of feature categories that should be considered by the MLT query.
   */
  private List<String> categories;

  /**
   * Constructor for the SimilarityQuery object.
   *
   * @param segmentId  SegmentId.
   * @param categories List of named feature categories that should be considered when doing
   *                   More-Like-This.
   * @param config     The {@link ReadableQueryConfig}. May be null!
   */
  @JsonCreator
  public MoreLikeThisQuery(@JsonProperty(value = "segmentId", required = true) String segmentId,
      @JsonProperty(value = "categories", required = true) List<String> categories,
      @JsonProperty(value = "config", required = false) QueryConfig config) {
    super(config);
    this.segmentId = segmentId;
    this.categories = categories;
  }

  /**
   * Getter for segmentId.
   *
   * @return String
   */
  public String getSegmentId() {
    return segmentId;
  }

  /**
   * Getter for categories.
   *
   * @return @return List of Strings
   */
  public List<String> getCategories() {
    return this.categories;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MessageType getMessageType() {
    return MessageType.Q_MLT;
  }

}


