package dataloader.interpreter;

import com.fasterxml.jackson.databind.node.ArrayNode;
import exception.DataLoaderException;

public interface JsonInterpreter {
     void process(ArrayNode jsonNodes) throws DataLoaderException;
}
