package org.vitrivr.cineast.core.features;

import java.util.ArrayList;
import java.util.List;

import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.data.FloatVectorImpl;
import org.vitrivr.cineast.core.data.Pair;
import org.vitrivr.cineast.core.data.SegmentContainer;
import org.vitrivr.cineast.core.data.StringDoublePair;
import org.vitrivr.cineast.core.features.abstracts.MotionHistogramCalculator;

public class SubDivMotionSumBackground3 extends MotionHistogramCalculator {

	public SubDivMotionSumBackground3() {
		super("features_SubDivMotionHistogramBackground3", "sums", 900f);
	}

	@Override
	public List<StringDoublePair> getSimilar(SegmentContainer sc, QueryConfig qc) {	
		Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(3, sc.getBgPaths());

		FloatVectorImpl fv = new FloatVectorImpl(pair.first);
		return getSimilar(fv.toArray(null), qc);
	}

}
