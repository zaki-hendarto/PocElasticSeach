package org.hendarto.poc.elasticsearch.controller;

import org.hendarto.poc.elasticsearch.document.AutoCompleteDoc;
import org.hendarto.poc.elasticsearch.document.Book;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.hendarto.poc.elasticsearch.document.SuggestWord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/search")
public class SeachEngineController {
    @Value("${elasticsearch.url}")
    public String elasticsearchUrl;

    private String indexName = "book";
    private int pageSize = 2;

    @GetMapping("/auto-complete/{keyword}")
    public AutoCompleteDoc autoComplete(@PathVariable final String keyword) {
        List<String> keywords = new ArrayList<>();
       String indexName = "book";
        ClientConfiguration config = ClientConfiguration.builder() .connectedTo(elasticsearchUrl) .build();
        RestHighLevelClient cli = RestClients.create(config).rest();
        SearchRequest request = new SearchRequest(indexName);
        MatchPhrasePrefixQueryBuilder query = QueryBuilders.matchPhrasePrefixQuery ("suggest", keyword);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(query);
       //builder.suggest(suggestion);
        request.source(builder);

        try {
            SearchResponse resp = cli.search(request, RequestOptions.DEFAULT);
            SearchHit[]  hits = resp.getHits().getHits();
            System.out.println("SIZE "+hits.length);
            for(SearchHit hit :hits ){
                String content = hit.getSourceAsString();
                System.out.println("RESULT "+content);
                GsonJsonParser gson = new GsonJsonParser();
                Map<String, Object> map = gson.parseMap(content);
                keywords.add(map.get("title").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AutoCompleteDoc doc= new AutoCompleteDoc();
        doc.setKeywords(keywords);
        return doc;
    }

    @GetMapping("/fuzzi/{page}/{sortby}/{keyword}")
    public List<Book> fuzziness(@PathVariable final int page, @PathVariable final String sortby, @PathVariable final String keyword) {
        System.out.println("PAGE "+page);
        System.out.println("SORTBY "+sortby);
        System.out.println("KEYWORD "+keyword);
        int from = page <= 0 ? 0 : page * pageSize;
        List<Book> books = new ArrayList<>();

        ClientConfiguration config = ClientConfiguration.builder() .connectedTo(elasticsearchUrl) .build();
        RestHighLevelClient cli = RestClients.create(config).rest();
        SearchRequest request = new SearchRequest(indexName);
        MultiMatchQueryBuilder multiMatch = new MultiMatchQueryBuilder(keyword,"title","description").fuzziness(Fuzziness.AUTO).operator(Operator.AND);
        SearchSourceBuilder builder = new SearchSourceBuilder().from(from).size(pageSize);
        builder.query(multiMatch);
        builder.sort(SortBuilders.fieldSort(sortby+".keyword").order(SortOrder.ASC));
        request.source(builder);

        try {
            SearchResponse resp = cli.search(request, RequestOptions.DEFAULT);
            SearchHit[]  hits = resp.getHits().getHits();
            System.out.println("SIZE "+hits.length);
            for(SearchHit hit :hits ){
                String content = hit.getSourceAsString();
                System.out.println("RESULT "+content);
                GsonJsonParser gson = new GsonJsonParser();
                Map<String, Object> map = gson.parseMap(content);
                Book book = new Book();
                book.setTitle(map.get("title").toString());
                book.setDescription(map.get("description").toString());
                books.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    @GetMapping("/suggest/{keyword}")
    public SuggestWord suggest(@PathVariable final String keyword) {
        List<String> keywords = new ArrayList<>();
        String indexName = "book";
        ClientConfiguration config = ClientConfiguration.builder() .connectedTo(elasticsearchUrl) .build();
        RestHighLevelClient cli = RestClients.create(config).rest();
        SearchRequest request = new SearchRequest(indexName);
        TermSuggestionBuilder suggestionBuilder = new TermSuggestionBuilder("suggest");
        suggestionBuilder.prefixLength(0);

        SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("suggestion", suggestionBuilder).setGlobalText(keyword);
        SearchSourceBuilder builder = new SearchSourceBuilder().suggest(suggestBuilder);
        request.source(builder);

        try {
            SearchResponse resp = cli.search(request, RequestOptions.DEFAULT);
            Suggest suggest = resp.getSuggest();
            Iterator iter = suggest.iterator();
            while(iter.hasNext()){
                Suggest.Suggestion suggestion =(Suggest.Suggestion)iter.next();
                List entries = suggestion.getEntries();
                for(int i = 0 ; i < entries.size();i++){
                    TermSuggestion.Entry entry = (TermSuggestion.Entry )entries.get(i);
                    String text = entry.getText().toString();
                    System.out.print("TEXT "+text);
                    if(entry.getOptions().size()>0){
                        keywords.add( entry.getOptions().get(0).getText().toString());
                        System.out.println(" SUGGEST  "+entry.getOptions().get(0).getText().toString());
                    }else{
                        keywords.add(text);
                        System.out.println("");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SuggestWord suggestWord = new SuggestWord();
        suggestWord.setSuggestion(keywords);
        return suggestWord;
    }
}
