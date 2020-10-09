package com.bigdata.demo.formal;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by charles on 2020/10/9.
 */
public class Indexer {

    private IndexWriter writer;

    public static void main(String[] args) {
        String indexDir = "";
        String dataDir = "";

        long timeStart = System.currentTimeMillis();
        //Indexer
    }

    public Indexer(String indexDir) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory,config);
    }
}
