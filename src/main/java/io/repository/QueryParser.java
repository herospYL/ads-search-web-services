package io.repository;

import java.util.List;

public interface QueryParser{
    List<String> QueryUnderstand(String query);
    List<List<String>> QueryRewrite(String query);
}
