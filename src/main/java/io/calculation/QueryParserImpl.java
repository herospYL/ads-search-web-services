package io.calculation;

import io.cache.CachePoolType;
import io.extra.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class QueryParserImpl implements QueryParser {

    private Utility utility;
    private StringRedisTemplate cache;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public QueryParserImpl(Utility utility, StringRedisTemplate cache) {
        this.utility = utility;
        this.cache = cache;
    }

    @Override
    public List<String> QueryUnderstand(String query) throws IOException {
        try {
            return utility.cleanedTokenize(query);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<List<String>> QueryRewrite(String query) throws IOException {
        List<List<String>> res = new ArrayList<>();

        try {
            List<String> tokens = utility.cleanedTokenize(query);
            String combinedKey = String.join(Utility.underscoreSeparator, tokens);

            String queryKey = Utility.getCacheKey(combinedKey, CachePoolType.synonyms);

            Set<String> synonyms = cache.opsForSet().members(queryKey);
            if (synonyms != null) {
                for (String synonym : synonyms) {
                    List<String> tokenList = new ArrayList<>();
                    String[] s = synonym.split(Utility.underscoreSeparator);
                    Collections.addAll(tokenList, s);
                    res.add(tokenList);
                }
            } else {
                res.add(tokens);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }

        return res;
    }
}
