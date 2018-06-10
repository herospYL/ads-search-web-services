package io.calculation;

import java.io.IOException;
import java.util.List;

public interface QueryParser{
    List<String> QueryUnderstand(String query) throws IOException;
    List<List<String>> QueryRewrite(String query) throws IOException;
}
