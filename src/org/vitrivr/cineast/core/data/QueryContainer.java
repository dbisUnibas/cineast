package org.vitrivr.cineast.core.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.vitrivr.cineast.core.decode.subtitle.SubtitleItem;
import org.vitrivr.cineast.core.util.MathHelper;

import georegression.struct.point.Point2D_F32;

public class QueryContainer implements SegmentContainer {

	private MultiImage img;
	private Frame frame;
	private ArrayList<SubtitleItem> subitem = new ArrayList<SubtitleItem>(1);
	private List<Pair<Integer, LinkedList<Point2D_F32>>> paths = new ArrayList<Pair<Integer, LinkedList<Point2D_F32>>>();
	private List<Pair<Integer, LinkedList<Point2D_F32>>> bgPaths = new ArrayList<Pair<Integer, LinkedList<Point2D_F32>>>();
	private ArrayList<String> tags = new ArrayList<>();
	private float relativeStart = 0, relativeEnd = 0, weight = 1f;
	private String id = null;
	
	public QueryContainer(MultiImage img){
		this.img = img;
	}
	
	@Override
	public MultiImage getAvgImg() {
		return this.img;
	}

	@Override
	public MultiImage getMedianImg() {
		return this.img;
	}

	@Override
	public Frame getMostRepresentativeFrame() {
		if(this.frame == null){
			int id = (getStart() + getEnd()) /2; 
			this.frame = new Frame(id, this.img);
		}
		return this.frame;
	}

	@Override
	public int getStart() {
		return 0;
	}

	@Override
	public int getEnd() {
		return 0;
	}

	@Override
	public List<SubtitleItem> getSubtitleItems() {
		return this.subitem;
	}

	@Override
	public float getRelativeStart() {
		return relativeStart;
	}

	@Override
	public float getRelativeEnd() {
		return relativeEnd;
	}
	
	public void setRelativeStart(float relativeStart){
		this.relativeStart = relativeStart;
	}
	
	public void setRelativeEnd(float relativeEnd){
		this.relativeEnd = relativeEnd;
	}

	@Override
	public List<Pair<Integer, LinkedList<Point2D_F32>>> getPaths() {
		return this.paths;
	}
	
	@Override
	public List<Pair<Integer, LinkedList<Point2D_F32>>> getBgPaths() {
		return this.bgPaths;
	}

	@Override
	public List<Frame> getFrames() {
		ArrayList<Frame> _return = new ArrayList<Frame>(1);
		_return.add(this.frame);
		return _return;
	}

	@Override
	public String getId() {
		return this.id == null ? "" : this.id;
	}

	public void setId(String id){
		this.id = id;
	}
	
	public boolean hasId(){
		return this.id != null;
	}
	
	@Override
	public String getSuperId() {
		return "";
	}
	
	public void addPath(LinkedList<Point2D_F32> path){
		this.paths.add(new Pair<Integer, LinkedList<Point2D_F32>>(0, path));
	}
	
	public void addBgPath(LinkedList<Point2D_F32> path){
		this.bgPaths.add(new Pair<Integer, LinkedList<Point2D_F32>>(0, path));
	}

	@Override
	public List<String> getTags() {
		return this.tags;
	}
	
	public void addTag(String tag){
		this.tags.add(tag);
	}
	
	/**
	 * weight used for relevance feedback
	 */
	public float getWeight(){
		return this.weight;
	}
	
	public void setWeight(float weight){
		if(Float.isNaN(weight)){
			this.weight = 0f;
			return;
		}
		this.weight = MathHelper.limit(weight, -1f, 1f);
	}

}
