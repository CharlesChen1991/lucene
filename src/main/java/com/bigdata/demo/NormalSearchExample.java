package com.bigdata.demo;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Created by charles on 2020/10/15.
 */
public class NormalSearchExample {

    private final String indexPath = "/Users/charles/personal/testindex";

    private final String[] TEXT_DATA = {
            "Whatever is worth doing is worth doing well.",
            "Happiness is a way station between too much and too little.",
            "In love folly is always sweet.",
            "The hard part isn’t making the decision. It’s living with it.",
            "You will have it if it belongs to you,whereas you don't kvetch for it if it doesn't appear in your life.",
            "　　When a cigarette falls in love with a match,it is destined to be hurt.",
            "the fact is that the world is out of everyone’s expectation. but some learn to forget, but others insist",
            "to the same word, is both miss, is also missed“miss”",
            "men love from overlooking while women love from looking up. if love isa mountain, then if men go up, more women they will see while womenwill see fewer men.",
            "love is hard to get into, but harder to get out of",
            "Do not keep anything for a special occasion, because every day that you live is a special occasion.",
            "Michael Jackson 《You Are Not Alone》: I can hear your prayers. Your burdens I will bear. But first I need your hand then forever can begin.",
            "When I thought I couldn’t go on, I forced myself to keep going. My success is based on persistence, not luck."
    };

    public Directory makeIndex() throws IOException {
        Directory inMemoryIndexDirectory = new MMapDirectory(Paths.get(indexPath));
        IndexWriterConfig conf = new IndexWriterConfig(new SimpleAnalyzer());
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter writer = new IndexWriter(inMemoryIndexDirectory,conf);

        for(int i = 0; i < TEXT_DATA.length; i++){
            Document document = new Document();
            document.add(new TextField("passage",TEXT_DATA[i], Field.Store.YES));
            writer.addDocument(document);
        }
        writer.close();
        return inMemoryIndexDirectory;
    }

    public void phraseSearch() throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        QueryParser parser = new QueryParser("passage", new SimpleAnalyzer());
        ScoreDoc scoreDoc = null;
        while (true) {
            System.out.println("请输入分页大小");
            int pageSize = Integer.parseInt(in.readLine());
            System.out.println("请输入操作");
            int operation = Integer.parseInt(in.readLine()); // 0-重新搜索；1-下一页
            System.out.println("请输查询语句");
            String phrase = in.readLine();
            Query query = parser.parse(phrase);

            TopFieldDocs docs ;
            if (0 == operation) {
                docs = searcher.search(query, pageSize,Sort.RELEVANCE,true);
            } else if (1 == operation) {
                docs = searcher.searchAfter(scoreDoc, query, pageSize,Sort.RELEVANCE,true);
            }else {
                continue;
            }

            if(docs.totalHits.value <= 0){
                System.out.println("未找到该记录");
                continue;
            }
            scoreDoc = docs.scoreDocs[docs.scoreDocs.length - 1];
            System.out.println("共找到记录详情条数:"+docs.totalHits.value);
            for (ScoreDoc d:docs.scoreDocs){
                FieldDoc fd = (FieldDoc)d;
                System.out.println("--  " + fd.shardIndex + "｜" +  fd.score + "｜" + fd.toString());
                Document document = searcher.doc(fd.doc);
                System.out.println("     " + document.toString());
            }
        }
    }
    public static void main(String[] args) throws IOException, ParseException {
        NormalSearchExample example = new NormalSearchExample();
        example.makeIndex();
        example.phraseSearch();
    }




}
