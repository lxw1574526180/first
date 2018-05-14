package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Test02 {

	/*
	 * 创建索引
	 */
	@Test
	public void addIndex() throws IOException {
		FSDirectory directory = FSDirectory.open(new File("e:\\IndexRespo1"));
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		File file = new File("E:\\课堂资料包\\师宜龙老师\\lucene第一天\\Lucene&solr-day01\\资料\\上课用的查询资料searchsource");
		File[] files = file.listFiles();
		for(File f : files) {
			Document document = new Document();
			Field name = new TextField("name", f.getName(), Store.YES);
			Field path = new StoredField("path", f.getPath());
			long sizeOf = FileUtils.sizeOf(f);
			Field size = new LongField("size", sizeOf, Store.YES);
			String fileToString = FileUtils.readFileToString(f);
			Field content = new TextField("content", fileToString,Store.YES);
			document.add(content);
			document.add(size);
			document.add(path);
			document.add(name);
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	
	@Test
	public void TestDeleteIndex() throws Exception {
		Directory  directory = FSDirectory.open(new File("e:\\IndexRespo1"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
	/*	indexWriter.deleteDocuments(new Term("name", "spring"));*/
		/*indexWriter.deleteDocuments();*/
		indexWriter.commit();
		indexWriter.close();
	}
	
	@Test
	public void TestUpdateIndex() throws Exception{
		Directory  directory = FSDirectory.open(new File("e:\\IndexRespo1"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory, config);
		Document doc = new Document();
		doc.add(new TextField("name", "自己再IndexWriterConfig一次亲自添加的一个文档spring",Store.YES));
		doc.add(new StoredField("path", "d://sdsds"));
		doc.add(new LongField("size", 100l,Store.YES));
		doc.add(new StringField("content", "自己添加的一个文档自己添加的一个文档自己添加的一个文档",Store.NO));
		
		//先查询，再删除，最后添加，（查询出来的document有三个，删除这三个，再添加我们新建的一个，最终文档个数减少两个）
		indexWriter.updateDocument(new Term("name", "apache"), doc);
		indexWriter.commit();
		indexWriter.close();
	}
	
	@Test
	public void testSearchIndex() throws Exception {
		Directory  directory = FSDirectory.open(new File("e:\\IndexRespo1"));
		//读取索引对象
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索索引的对象
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//执行查询
		//根据字段查询
		TermQuery query = new TermQuery(new Term("content", "spring"));
		//范围查询（文件大小区间）
		/*NumericRangeQuery<Long> query = NumericRangeQuery.newLongRange("size", 0l,1000l, true, true);*/
		//组合查询
		/*BooleanQuery query = new BooleanQuery();
		Query query1 = new TermQuery(new Term("name","apache"));
		Query query2 = new TermQuery(new Term("content", "apache"));
		query.add(query1,Occur.MUST);
		query.add(query2, Occur.MUST);*/
		
		
		
		//查询所有
//		Query query = new MatchAllDocsQuery();
		System.out.println("查询语法"+query);
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数为："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(ScoreDoc score : scoreDocs) {
			int docId = score.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
			System.out.println(doc.get("content"));
		}
		indexReader.close();
	}
	
	@Test
	public void insearchParser() throws Exception {
		Directory  directory = FSDirectory.open(new File("e:\\IndexRespo1"));
		//读取索引对象
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索索引的对象
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//单个域的查询
		/*QueryParser queryParser = new QueryParser("name",new IKAnalyzer());
		Query query = queryParser.parse("spring is a project");*/
		//多个域的查询
		QueryParser queryParser = new MultiFieldQueryParser(new String[] {"name", "content"},new IKAnalyzer());
		Query query = queryParser.parse("spring is a project");
		
		System.out.println("查询语法"+query);
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数为："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(ScoreDoc score : scoreDocs) {
			int docId = score.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
//			System.out.println(doc.get("content"));
		}
		indexReader.close();
	}
	

}
