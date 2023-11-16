package org.serviceEngineering.adrViewer.div;

import java.io.IOException;

public final class GraphqlSchemaReaderUtil {

    /**
     * Reads and retrieves the content of a GraphQL schema file by its filename.
     * This method loads the content of the specified GraphQL schema file from the resources directory.
     *
     * @param filename The name of the GraphQL schema file to retrieve (without the file extension).
     * @return The content of the specified GraphQL schema file as a String.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    public static String getSchemaFromFileName(final String filename) throws IOException {
        return new String(
                GraphqlSchemaReaderUtil.class.getClassLoader().getResourceAsStream("graphql/" + filename + ".graphql").readAllBytes());

    }

}
