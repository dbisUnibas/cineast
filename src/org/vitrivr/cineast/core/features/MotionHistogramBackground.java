package org.vitrivr.cineast.core.features;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.vitrivr.adam.grpc.AdamGrpc.AttributeType;
import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.data.FloatVectorImpl;
import org.vitrivr.cineast.core.data.Pair;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.SegmentContainer;
import org.vitrivr.cineast.core.data.StringDoublePair;
import org.vitrivr.cineast.core.db.PersistentTuple;
import org.vitrivr.cineast.core.features.abstracts.SubDivMotionHistogram;
import org.vitrivr.cineast.core.setup.EntityCreator;
import org.vitrivr.cineast.core.setup.EntityCreator.AttributeDefinition;
import org.vitrivr.cineast.core.util.MathHelper;

public class MotionHistogramBackground extends SubDivMotionHistogram {

	public MotionHistogramBackground() {
		super("features_MotionHistogramBackground", "feature", MathHelper.SQRT2);
	}
	
	@Override
	public void processShot(SegmentContainer shot) {
		if(!phandler.idExists(shot.getId())){
			
			Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(1, shot.getBgPaths());
			
			double sum = pair.first.get(0);
			FloatVectorImpl fv = new FloatVectorImpl(pair.second.get(0));

			persist(shot.getId(), sum, fv);
		}
	}

	protected void persist(String shotId, double sum, ReadableFloatVector fs) {
		PersistentTuple tuple = this.phandler.generateTuple(shotId, sum, fs);
		this.phandler.persist(tuple);
	}

	@Override
	public List<StringDoublePair> getSimilar(SegmentContainer sc, QueryConfig qc) {
		Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(1, sc.getBgPaths());
		
		FloatVectorImpl fv = new FloatVectorImpl(pair.second.get(0));
		return getSimilar(fv.toArray(null), qc);
	}

	@Override
	public void initalizePersistentLayer(Supplier<EntityCreator> supply) {
		supply.get().createFeatureEntity("features_MotionHistogramBackground", true, new AttributeDefinition("sum", AttributeType.FLOAT), new AttributeDefinition("hist", AttributeType.FEATURE));
	}

	
}
