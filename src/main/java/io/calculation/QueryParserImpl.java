package io.calculation;

import io.cache.CachePoolType;
import io.extra.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class QueryParserImpl implements QueryParser {

    private Utility utility;
    private RedisTemplate<String, Object> cache;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public QueryParserImpl(Utility utility, RedisTemplate<String, Object> cache) {
        this.utility = utility;
        this.cache = cache;
    }

    @Override
    public List<String> QueryUnderstand(String query) throws IOException {
        try {
            return utility.cleanedTokenize(query);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<List<String>> QueryRewrite(String query) throws IOException {
        List<List<String>> res = new ArrayList<>();

        try {
            List<String > tokens = utility.cleanedTokenize(query);
            String combinedKey = String.join(Utility.underscoreSeparator, tokens);

            String queryKey = Utility.getCacheKey(combinedKey, CachePoolType.synonyms);

            Object queryObj = cache.opsForValue().get(queryKey);
            if (queryObj instanceof List) {
                try {
                    @SuppressWarnings("unchecked")
                    List<String>  synonyms = (ArrayList<String>)queryObj;
                    for(String synonym : synonyms) {
                        List<String> token_list = new ArrayList<>();
                        String[] s = synonym.split(Utility.underscoreSeparator);
                        Collections.addAll(token_list, s);
                        res.add(token_list);
                    }
                }
                catch (ClassCastException ce) {
                    logger.warn(ce.getMessage());
                    res.add(tokens);
                }
            }
            else {
                res.add(tokens);
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }

        return res;
    }
}
