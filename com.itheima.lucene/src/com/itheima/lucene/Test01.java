package com.itheima.lucene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Test01 {

	@Test
	public void TestAddIndex() throws Exception {
		// 1、执行索引库的目录
		FSDirectory directory = FSDirectory.open(new File("e:\\IndexRespo"));
		// 2、指定分词器 --标准分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 3、创建一个配置对象
		Version matchVersion = Version.LATEST;
		IndexWriterConfig config = new IndexWriterConfig(matchVersion, analyzer);
		// 4、创建一个 写入索引对象
		IndexWriter writer = new IndexWriter(directory, config);
		// 5、写入document对象
		File file = new File("E:\\课堂资料包\\师宜龙老师\\lucene第一天\\Lucene&solr-day01\\资料\\上课用的查询资料searchsource");
		File[] files = file.listFiles();
		for (File f : files) {
			Document doc = new Document();
			// field 域 等同于属性
			// 1.create web page.txt
			// 1
			// create
			// web
			// page
			// txt
			// 文件名称
			TextField name = new TextField("name", f.getName(), Store.YES);
			// 文件路径
			Field path = new TextField("path", f.getPath(), Store.YES);
			// 文件内容
			String fileContent = FileUtils.readFileToString(f);
			TextField content = new TextField("content", fileContent, Store.YES);
			// 文件大小
			long sizeOf = FileUtils.sizeOf(f);
			TextField size = new TextField("size", sizeOf + "", Store.YES);
			doc.add(content);
			doc.add(name);
			doc.add(path);
			doc.add(size);

			writer.addDocument(doc);
		}
		// 6、关闭IndexWriter对象
		writer.close();

	}

	@Test
	public void testSearchIndex() throws Exception {
		// 1、指定索引库的目录
		Directory directory = FSDirectory.open(new File("e:\\IndexRespo"));
		// 2、创建一个读取索引对象
		IndexReader reader = DirectoryReader.open(directory);
		// 3、创建一个搜索索引的对象
		IndexSearcher searcher = new IndexSearcher(reader);
		// 4、执行查询
		Query query = new TermQuery(new Term("content", "spring"));
		TopDocs topDdocs = searcher.search(query, 100);
		System.out.println("总条数" + topDdocs.totalHits);// 总条数
		ScoreDoc[] scoreDocs = topDdocs.scoreDocs;// 获取所有document的id
		System.out.println(scoreDocs);
		// 5、获取结果
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document d = searcher.doc(docId);
			System.out.println(d.get("name"));
			System.out.println(d.get("path"));
			System.out.println(d.get("size"));
			System.out.println(d.get("content"));

		}
		// 6、关闭资源
		reader.close();
	}

	@Test
	public void testAnalyzer() throws Exception {
		/* Analyzer analyzer = new StandardAnalyzer(); */
		Analyzer analyzer = new IKAnalyzer();

		String str = "The Spring Framework provides a comprehensive programming and configuration model.";
		// 获得tokenStream对象
		// 第一个参数：域名，可以随便给一个
		// 第二个参数：要分析的文本内容
		TokenStream tokenStream = analyzer.tokenStream("test", str);
		// 添加一个引用，可以获得每个关键词
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		// 添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
		OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
		//将指针调整到列表的头部
		tokenStream.reset();
		//遍历关键词列表，通过incrementToken方法判断列表是否结束
		while(tokenStream.incrementToken()) {
			//关键词的起始位置
			System.out.println("start->" + offsetAttribute.startOffset());
			//取关键词
			System.out.println(charTermAttribute);
			//结束位置
			System.out.println("end->" + offsetAttribute.endOffset());
		}
		tokenStream.close();
	}

}
