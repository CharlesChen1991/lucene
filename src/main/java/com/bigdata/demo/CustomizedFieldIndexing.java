package com.bigdata.demo;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by charles on 2020/10/9.
 */
public class CustomizedFieldIndexing {

    private String[] ids = {"1","2","3","4"};

    private String[] unIndexFieldInfo = {"uiField1 ama","uiField2 bmb","uiField3 cmc","uiField4 ama"};

    private String[] unStoredFieldInfo = {"Whatever is worth doing is worth doing well.",
            "Happiness is a way station between too much and too little.",
            "In love folly is always sweet.",
            "The hard part isn’t making the decision. It’s living with it."
    };

    private String[] stringFieldInfo = {"red","blue","red","green"};

    private Directory inMemoryIndexDirectory = new RAMDirectory();

    private void setupIndex(String indexPath,boolean tokenized) throws IOException {
        inMemoryIndexDirectory = new MMapDirectory(Paths.get(indexPath));
        IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter writer = new IndexWriter(inMemoryIndexDirectory,conf);
        FieldType fieldTypeNotIndex = new FieldType();
        fieldTypeNotIndex.setStored(true);
        fieldTypeNotIndex.setIndexOptions(IndexOptions.NONE);
        fieldTypeNotIndex.setTokenized(tokenized);

        FieldType fieldTypeNotStore = new FieldType();
        fieldTypeNotStore.setStored(false);
        fieldTypeNotStore.setIndexOptions(IndexOptions.DOCS);
        fieldTypeNotStore.setTokenized(tokenized);

        FieldType fieldTypeNotStoreButFreqs = new FieldType();
        fieldTypeNotStoreButFreqs.setStored(false);
        fieldTypeNotStoreButFreqs.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        fieldTypeNotStoreButFreqs.setTokenized(true);


        for(int i = 0; i < ids.length; i++){
            Document document = new Document();
            document.add(new Field("id",ids[i],fieldTypeNotIndex));
            document.add(new Field("field1",unIndexFieldInfo[i],fieldTypeNotIndex));
            document.add(new Field("field2",unStoredFieldInfo[i],fieldTypeNotStore));
            document.add(new Field("field3",unStoredFieldInfo[i],fieldTypeNotStoreButFreqs));
            document.add(new StringField("color",stringFieldInfo[i], Field.Store.YES));
            writer.addDocument(document);
        }
        writer.close();
    }

    public long getHitCount(String fieldName,String searchData) throws IOException {
        IndexReader reader = DirectoryReader.open(inMemoryIndexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Term t = new Term(fieldName,searchData);
        Query query = new TermQuery(t);
        TopDocs docs = searcher.search(query,1);
        return docs.totalHits.value;
    }

    public static void main(String[] args) throws IOException {
        CustomizedFieldIndexing indexing = new CustomizedFieldIndexing();
        //默认不经过分析器
        indexing.setupIndex("/Users/charles/personal/testindex",false);
        long count = indexing.getHitCount("color","red");
        System.out.println("Got " + count);
        count = indexing.getHitCount("field1","ama");
        System.out.println("Got " + count);
        count = indexing.getHitCount("field2","Whatever is worth doing is worth doing well.");
        System.out.println("Got " + count);
        count = indexing.getHitCount("field2","Whatever");
        System.out.println("Got " + count);
        count = indexing.getHitCount("field3","doing");
        System.out.println("Got " + count);
    }

}
