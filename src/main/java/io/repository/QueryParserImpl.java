package io.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QueryParserImpl implements QueryParser {

    @Override
    public List<String> QueryUnderstand(String query) {
        return null;
    }

    @Override
    public List<List<String>> QueryRewrite(String query) {
        return null;
    }
}
