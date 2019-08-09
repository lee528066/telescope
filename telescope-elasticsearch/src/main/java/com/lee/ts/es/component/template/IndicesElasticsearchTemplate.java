/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lee.ts.es.component.template;

import com.lee.ts.es.component.IndexSplitStrategy;
import com.lee.ts.es.component.anotation.Indices;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.facet.FacetRequest;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 重新写了ElasticsearchTemplate，实现基于多个index的创建和查询
 * {@link com.lee.ts.es.component.IndexSplitStrategy}
 * @author liwei
 */
@Slf4j
public class IndicesElasticsearchTemplate implements ApplicationContextAware, InitializingBean {

	@Resource
	private IndexSplitStrategy indexSplitStrategy;

	private final StandardEvaluationContext context;

	private ElasticsearchTemplate elasticsearchTemplate;

	private ResultsMapper resultsMapper;

	private Client client;

	private String searchTimeout;

	public IndicesElasticsearchTemplate(Client client,
										ElasticsearchConverter converter,
										ElasticsearchTemplate elasticsearchTemplate) {
		this.context = new StandardEvaluationContext();
		this.client = client;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.resultsMapper = new DefaultResultMapper(converter.getMappingContext());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context.addPropertyAccessor(new BeanFactoryAccessor());
		context.setBeanResolver(new BeanFactoryResolver(applicationContext));
		context.setRootObject(applicationContext);
	}


	@Override
	public void afterPropertiesSet() {
		indexSplitStrategy.initIndicesCache(getNonKibanaIndices());
	}

	/**
	 * 获取所有的业务索引（排除kibana的系统索引）
	 * @return 业务索引集合
	 */
	private Set<String> getNonKibanaIndices() {
		return client.admin().indices().stats(new IndicesStatsRequest().all())
					.actionGet().getIndices().keySet();
	}

	public void setSearchTimeout(String searchTimeout) {
		this.searchTimeout = searchTimeout;
	}

	private static final String FIELD_SCORE = "_score";

	public <T> AggregatedPage<T> queryForPage(SearchQuery query, Class<T> clazz) {
		return queryForPage(query, clazz, resultsMapper);
	}

	public <T> AggregatedPage<T> queryForPage(SearchQuery query, Class<T> clazz, SearchResultMapper mapper) {
		SearchResponse response = doSearch(prepareSearch(query, clazz), query);
		return mapper.mapResults(response, clazz, query.getPageable());
	}

	/**
	 * 批量创建index集合，只有当index不存在的时候才会新建
	 * @param indexNames indexName集合
	 */
	public void createIndicesIfNotExists(Set<String> indexNames){
		indexNames.forEach(indexName -> {
			if (!elasticsearchTemplate.indexExists(indexName)) {
				elasticsearchTemplate.createIndex(indexName);
				log.info("新建index：{}", indexName);
			}
		});
	}

	private SearchResponse doSearch(SearchRequestBuilder searchRequest, SearchQuery searchQuery) {
		if (searchQuery.getFilter() != null) {
			searchRequest.setPostFilter(searchQuery.getFilter());
		}

		if (!isEmpty(searchQuery.getElasticsearchSorts())) {
			for (SortBuilder sort : searchQuery.getElasticsearchSorts()) {
				searchRequest.addSort(sort);
			}
		}

		if (!searchQuery.getScriptFields().isEmpty()) {
			// _source should be return all the time
			// searchRequest.addStoredField("_source");
			for (ScriptField scriptedField : searchQuery.getScriptFields()) {
				searchRequest.addScriptField(scriptedField.fieldName(), scriptedField.script());
			}
		}

		if (searchQuery.getHighlightFields() != null || searchQuery.getHighlightBuilder() != null) {
			HighlightBuilder highlightBuilder = searchQuery.getHighlightBuilder();
			if (highlightBuilder == null) {
				highlightBuilder = new HighlightBuilder();
			}
			for (HighlightBuilder.Field highlightField : searchQuery.getHighlightFields()) {
				highlightBuilder.field(highlightField);
			}
			searchRequest.highlighter(highlightBuilder);
		}

		if (!isEmpty(searchQuery.getIndicesBoost())) {
			for (IndexBoost indexBoost : searchQuery.getIndicesBoost()) {
				searchRequest.addIndexBoost(indexBoost.getIndexName(), indexBoost.getBoost());
			}
		}

		if (!isEmpty(searchQuery.getAggregations())) {
			for (AbstractAggregationBuilder aggregationBuilder : searchQuery.getAggregations()) {
				searchRequest.addAggregation(aggregationBuilder);
			}
		}

		if (!isEmpty(searchQuery.getFacets())) {
			for (FacetRequest aggregatedFacet : searchQuery.getFacets()) {
				searchRequest.addAggregation(aggregatedFacet.getFacet());
			}
		}
		return getSearchResponse(searchRequest.setQuery(searchQuery.getQuery()));
	}

	private SearchResponse getSearchResponse(SearchRequestBuilder requestBuilder) {
		return getSearchResponse(requestBuilder.execute());
	}

	private SearchResponse getSearchResponse(ActionFuture<SearchResponse> response) {
		return searchTimeout == null ? response.actionGet() : response.actionGet(searchTimeout);
	}

	private <T> SearchRequestBuilder prepareSearch(Query query, Class<T> clazz) {
		setPersistentEntityIndexAndType(query, clazz);
		return prepareSearch(query);
	}

	private SearchRequestBuilder prepareSearch(Query query) {
		Assert.notNull(query.getIndices(), "No index defined for Query");
		Assert.notNull(query.getTypes(), "No type defined for Query");

		int startRecord = 0;
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(toArray(query.getIndices()))
				.setSearchType(query.getSearchType())
				.setTypes(toArray(query.getTypes()))
				.setVersion(true)
				.setTrackScores(query.getTrackScores());

		if (query.getSourceFilter() != null) {
			SourceFilter sourceFilter = query.getSourceFilter();
			searchRequestBuilder.setFetchSource(sourceFilter.getIncludes(), sourceFilter.getExcludes());
		}

		if (query.getPageable().isPaged()) {
			startRecord = query.getPageable().getPageNumber() * query.getPageable().getPageSize();
			searchRequestBuilder.setSize(query.getPageable().getPageSize());
		}
		searchRequestBuilder.setFrom(startRecord);

		if (!query.getFields().isEmpty()) {
			searchRequestBuilder.setFetchSource(toArray(query.getFields()), null);
		}

		if (query.getIndicesOptions() != null) {
			searchRequestBuilder.setIndicesOptions(query.getIndicesOptions());
		}

		if (query.getSort() != null) {
			for (Sort.Order order : query.getSort()) {
				SortOrder sortOrder = order.getDirection().isDescending() ? SortOrder.DESC : SortOrder.ASC;

				if (FIELD_SCORE.equals(order.getProperty())) {
					ScoreSortBuilder sort = SortBuilders //
							.scoreSort() //
							.order(sortOrder);

					searchRequestBuilder.addSort(sort);
				} else {
					FieldSortBuilder sort = SortBuilders //
							.fieldSort(order.getProperty()) //
							.order(sortOrder);

					if (order.getNullHandling() == Sort.NullHandling.NULLS_FIRST) {
						sort.missing("_first");
					} else if (order.getNullHandling() == Sort.NullHandling.NULLS_LAST) {
						sort.missing("_last");
					}

					searchRequestBuilder.addSort(sort);
				}
			}
		}

		if (query.getMinScore() > 0) {
			searchRequestBuilder.setMinScore(query.getMinScore());
		}
		return searchRequestBuilder;
	}

	private String[] toArray(List<String> values) {
		String[] valuesAsArray = new String[values.size()];
		return values.toArray(valuesAsArray);
	}

	private void setPersistentEntityIndexAndType(Query query, Class clazz) {
		if (query.getIndices().isEmpty()) {
			query.addIndices(retrieveIndexNameFromPersistentEntity(clazz));
		}
		if (query.getTypes().isEmpty()) {
			query.addTypes(retrieveTypeFromPersistentEntity(clazz));
		}
	}

	private String[] retrieveIndexNameFromPersistentEntity(Class clazz) {
		Indices indices = (Indices) clazz.getAnnotation(Indices.class);
		if(Objects.nonNull(indices)){
			String indexName = indices.indexName();
			SpelExpressionParser parser = new SpelExpressionParser();
			Expression expression = parser.parseExpression(indexName, ParserContext.TEMPLATE_EXPRESSION);
			String indexStr = expression.getValue(context, String.class);
			log.info("解析后的indies：{}", indexStr);
			Assert.notNull(indexStr, "表达式解析有误");
			return indexStr.split(",");
		}
		throw new IllegalArgumentException("该class未指定Indices注解");
	}

	private String[] retrieveTypeFromPersistentEntity(Class clazz) {
		if (clazz != null) {
			return new String[] { elasticsearchTemplate.getPersistentEntityFor(clazz).getIndexType() };
		}
		return null;
	}

}
