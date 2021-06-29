package model;

import java.util.logging.Logger;

public class RatingStrategy implements SearchStrategy {
    static private final Logger logger = Log.getInstance().getLogger();


    @Override
    public String buildSelect(String[] keywords, String location) {
        logger.info("Building SQL query based on ratings...");
        String query = "SELECT to_jsonb(array_agg(list)) FROM(SELECT name , museum_id, avg(coalesce(review_score, 0)) as average FROM " +
                "(SELECT  name, museum_id, score FROM (SELECT (";
        for (String keyword : keywords) {
            query = query.concat(String.format("ts_rank_cd(P.description_tsv, to_tsquery('italian', '%s'))/(1 + " +
                            "(SELECT sum(ts_rank_cd(P.description_tsv, to_tsquery('italian', '%s'))) FROM museums as P)) + ",
                    keyword, keyword));
        }
        query = query.substring(0, query.length() - 3).concat(") AS score, P.name, P.museum_id FROM museums as P) S WHERE score > 0 " +
                "ORDER BY score DESC LIMIT 50) list LEFT JOIN reviews ON fk_museum_id = list.museum_id GROUP BY museum_id, name, score " +
                "ORDER BY average DESC, score DESC) list;");
        logger.fine("QUERY:" + query);
        return query;
    }
}
