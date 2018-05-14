package cn.itcast.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrManager {
	
//	增加
	@Test
	public void testAdd() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		for (int i = 11; i < 21; i++) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", i+"");
			doc.addField("name", "solr添加标题"+i);
			doc.addField("content", "solr添加内容"+i);
			solrServer.add(doc);	
		}
		
		solrServer.commit();
	}
	
	 
//	修改
	@Test
	public void testUpdate() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
//		自动覆盖  如果id一样就会覆盖
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "1");
		doc.addField("name", "solr添加标题-修改后");
		doc.addField("content", "solr添加内容-修改后");
		solrServer.add(doc);	
		solrServer.commit();
	}
	
//	删除
	@Test
	public void testDELETE() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
//		solrServer.deleteById("1");
		solrServer.deleteByQuery("name:solr添加标题2");
//		solr  添 加 标 题 2
		solrServer.commit();
	}
//	查询
	@Test
	public void testQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("name:solr");
//		solrQuery.set("q", "solr");
		QueryResponse queryResponse = solrServer.query(solrQuery);
		
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		
		System.out.println("总条数："+solrDocumentList.getNumFound());
		
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("name"));
			System.out.println(solrDocument.get("content"));
		}
	}
	

}
