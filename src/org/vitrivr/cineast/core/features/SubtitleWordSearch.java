package org.vitrivr.cineast.core.features;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.data.SegmentContainer;
import org.vitrivr.cineast.core.data.StringDoublePair;
import org.vitrivr.cineast.core.db.DBSelector;
import org.vitrivr.cineast.core.db.DBSelectorSupplier;
import org.vitrivr.cineast.core.features.retriever.Retriever;
import org.vitrivr.cineast.core.setup.EntityCreator;

public class SubtitleWordSearch implements Retriever {

	private DBSelector selector;
	private static final float MAX_DIST = 1;
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public void init(DBSelectorSupplier supply) {
		this.selector = supply.get();
	}

//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc, String resultCacheName) {
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		StringBuffer buf = new StringBuffer();
//		ArrayList<LongDoublePair> result;
//		
//		if(!qc.getSubtitleItems().isEmpty()){
//			for(SubtitleItem item : qc.getSubtitleItems()){
//				String[] text = item.getText().split("\\s+");//split by whitespace
//				for(String word : text){
//					word = word.trim();
//					if(word.isEmpty()){
//						continue;
//					}
//					buf.append(word);
//					buf.append(" | ");
//				}
//				int buflen = buf.length();
//				if(buflen >= 3){
//					buf.delete(buflen - 3, buflen);
//				}
//			}
//			result = getText(buf.toString(), limit, resultCacheName);
//		}else{
//			result = new ArrayList<LongDoublePair>(1);
//		}
//		
//		return result;
//	}

//	private ArrayList<LongDoublePair> getText(String text, int limit, String resultCacheName){
//		ArrayList<LongDoublePair> result = new ArrayList<>(limit);
//		//http://www.postgresql.org/docs/9.1/static/textsearch-controls.html
//		String query;
//		if(resultCacheName == null){
//			query = "WITH q AS ("
//					+ "SELECT to_tsquery('" + ADAMTuple.escape(text) + "') AS query), "
//					+ "ranked AS( "
//					+ "SELECT shotid, text, ts_rank_cd(tsv, query) AS rank "
//					+ "FROM features.fulltext, q "
//					+ "WHERE q.query @@ tsv "
//					+ "ORDER BY rank DESC "
//					+ "LIMIT " + limit + ") "
//					+ "SELECT shotid, rank "
//					+ "FROM ranked, q "
//					+ "ORDER BY ranked DESC";
//		}else{
//			query = "WITH c AS (SELECT shotid AS filter FROM cineast.resultcacheelements, cineast.resultcachenames WHERE "
//					+ "resultcacheelements.chacheid = resultcachenames.id AND resultcachenames.name = '"
//					+ resultCacheName 
//					+ "'), q AS ("
//					+ "SELECT to_tsquery('" + ADAMTuple.escape(text) + "') AS query), "
//					+ "ranked AS( "
//					+ "SELECT shotid, text, ts_rank_cd(tsv, query) AS rank "
//					+ "FROM features.fulltext, q, c "
//					+ "WHERE q.query @@ tsv "
//					+ "AND shotid = c.filter "
//					+ "ORDER BY rank DESC "
//					+ "LIMIT " + limit + ") "
//					+ "SELECT shotid, rank "
//					+ "FROM ranked, q "
//					+ "ORDER BY ranked DESC";
//		}
//		
//		ResultSet rset = this.selector.select(query);
//		if(rset != null){
//			try {
//				while(rset.next()){
//					double dist = rset.getDouble(2);
//					long shotId = rset.getLong(1);
//
//					dist = 1 - (dist / MAX_DIST);
//					dist = Math.max(0, dist);
//					result.add(new LongDoublePair(shotId, dist));
//				}
//			} catch (SQLException e) {
//				LOGGER.fatal(LogHelper.SQL_MARKER, LogHelper.getStackTrace(e));
//			}
//		}
//		return result;
//	}
	
	@Override
	public void finish() {
		if(this.selector != null){
			this.selector.close();
		}
		
	}

	@Override
	public List<StringDoublePair> getSimilar(SegmentContainer sc, QueryConfig qc) {
		// TODO Auto-generated method stub
		return new ArrayList<>(0);
	}

	@Override
	public List<StringDoublePair> getSimilar(String shotId, QueryConfig qc) {
		// TODO Auto-generated method stub
		return new ArrayList<>(0);
	}

	@Override
	public void initalizePersistentLayer(Supplier<EntityCreator> supply) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public List<LongDoublePair> getSimilar(long shotId, String resultCacheName) {
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		ResultSet rset = this.selector.select("select text from features.fulltext where shotid = " + shotId);
//		if(rset != null){
//			try{
//				StringBuilder sb = new StringBuilder();
//				if(rset.next()){
//					String text = rset.getString(1);
//					String[] words = text.split("\\s+");
//					
//					for(String word : words){
//						sb.append(word);
//						sb.append(" | ");
//					}
//					int buflen = sb.length();
//					sb.delete(buflen - 3, buflen);
//				}
//				List<LongDoublePair> result = getText(sb.toString(), limit, resultCacheName);
//				return result;
//			}catch(SQLException e){
//				LOGGER.fatal(LogHelper.SQL_MARKER, LogHelper.getStackTrace(e));
//			}
//		}
//		return new ArrayList<LongDoublePair>(1);
//		
//	}

	

}
