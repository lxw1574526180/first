package cn.itcast.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrManager {
	
//	查询
	@Test
	public void testQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		SolrQuery solrQuery = new SolrQuery();
//		"params": {
//	     "q": "花儿",
//		"df": "product_name",
		
//		"product_catalog_name:美味厨房",
//		"product_price:[* TO 10]"
		
//	      "start": "0",
//	      "rows": "10",
//	      "sort": "product_price asc",
//		      "hl": "true",
//		      "hl.simple.pre": "<span style='color:red'>",
//		      "hl.simple.post": "</span>",
//		      "hl.fl": "product_name",
		
		solrQuery.set("q", "小黄人");
		solrQuery.set("df", "product_keywords");
//		solrQuery.addFilterQuery("product_catalog_name:美味厨房");
//		solrQuery.addFilterQuery("product_price:[* TO 10]");
		solrQuery.setStart(0);
		solrQuery.setRows(10);
		solrQuery.addSort("product_price", ORDER.asc);
		
		solrQuery.setHighlight(true);
		solrQuery.setHighlightSimplePre("<span style=\"color:red\">");
		solrQuery.setHighlightSimplePost("</span>");
		solrQuery.addHighlightField("product_name");
		
		QueryResponse queryResponse = solrServer.query(solrQuery);
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
//		"highlighting": {
//		    "3": {
//		      "product_name": [
//		        "神偷奶爸电影同款 惨叫发泄公仔 发声<span style='color:red'>小黄人</span>"
//		      ]
//		    },
//		    "554": {},
//		    }
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		
		System.out.println("总条数："+solrDocumentList.getNumFound());
		
		for (SolrDocument solrDocument : solrDocumentList) {
			Map<String, List<String>> map = highlighting.get(solrDocument.get("id"));
			List<String> list = map.get("product_name");
			String product_name = "";
			if(list!=null&&list.size()>0) {
				product_name = list.get(0);
			}else {
				product_name= (String) solrDocument.get("product_name");
			}
			
			System.out.println(solrDocument.get("id"));
			System.out.println(product_name);
			System.out.println(solrDocument.get("product_price"));
			System.out.println(solrDocument.get("product_picture"));
			System.out.println(solrDocument.get("product_catalog_name"));
		}
	}
	

}
