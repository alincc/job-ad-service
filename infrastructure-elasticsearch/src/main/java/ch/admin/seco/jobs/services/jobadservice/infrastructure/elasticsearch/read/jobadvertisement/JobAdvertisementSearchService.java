package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.ElasticsearchConfiguration;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.jobadvertisement.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.jobadvertisement.JobAdvertisementElasticsearchRepository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_RESTRICTED;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.TYPE_JOB_ADVERTISEMENT;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class JobAdvertisementSearchService {
    public enum SearchSort {
        score,
        date_asc,
        date_desc
    }

    private static final String PATH_CTX = "jobAdvertisement.";
    private static final String PATH_AVAM_JOB_ID = PATH_CTX + "stellennummerAvam";
    private static final String PATH_COMPANY_NAME = PATH_CTX + "jobContent.company.name";
    private static final String PATH_DESCRIPTION = PATH_CTX + "jobContent.jobDescriptions.description";
    private static final String PATH_EGOV_JOB_ID = PATH_CTX + "fingerprint";
    private static final String PATH_LOCATION_CANTON_CODE = PATH_CTX + "jobContent.location.cantonCode";
    private static final String PATH_LOCATION_COMMUNAL_CODE = PATH_CTX + "jobContent.location.communalCode";
    private static final String PATH_LOCATION_REGION_CODE = PATH_CTX + "jobContent.location.regionCode";
    private static final String PATH_OCCUPATIONS = PATH_CTX + "jobContent.occupations";
    private static final String PATH_OCCUPATIONS_AVAM_OCCUPATION_CODE = PATH_OCCUPATIONS + ".avamOccupationCode";
    private static final String PATH_OCCUPATIONS_BFS_CODE = PATH_OCCUPATIONS + ".bfsCode";
    private static final String PATH_OCCUPATIONS_SBN3_CODE = PATH_OCCUPATIONS + ".sbn3Code";
    private static final String PATH_OCCUPATIONS_SBN5_CODE = PATH_OCCUPATIONS + ".sbn5Code";
    private static final String PATH_X28_CODE = PATH_CTX + "jobContent.x28OccupationCodes";
    private static final String PATH_PERMANENT = PATH_CTX + "jobContent.employment.permanent";
    private static final String PATH_PUBLICATION_START_DATE = PATH_CTX + "publication.startDate";
    private static final String PATH_TITLE = PATH_CTX + "jobContent.jobDescriptions.title";
    private static final String PATH_STATUS = PATH_CTX + "status";
    private static final String PATH_SOURCE_SYSTEM = PATH_CTX + "sourceSystem";
    private static final String PATH_WORKLOAD_PERCENTAGE_MAX = PATH_CTX + "jobContent.employment.workloadPercentageMax";
    private static final String PATH_WORKLOAD_TIME_PERCENTAGE_MIN = PATH_CTX + "jobContent.employment.workloadPercentageMin";
    private static final String PATH_REPORTING_OBLIGATION = PATH_CTX + "reportingObligation";
    private static final int ONLINE_SINCE_DAYS = 60;

    private final CurrentUserContext currentUserContext;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ResultsMapper resultsMapper;
    private final JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    public JobAdvertisementSearchService(CurrentUserContext currentUserContext,
                                         ElasticsearchTemplate elasticsearchTemplate,
                                         ElasticsearchConfiguration.CustomEntityMapper customEntityMapper,
                                         JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository) {
        this.currentUserContext = currentUserContext;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.resultsMapper = new DefaultResultMapper(elasticsearchTemplate.getElasticsearchConverter().getMappingContext(), customEntityMapper);
        this.jobAdvertisementElasticsearchRepository = jobAdvertisementElasticsearchRepository;
    }

    public Page<JobAdvertisementDto> search(JobAdvertisementSearchRequest jobSearchRequest, int page, int size, SearchSort sort) {
        Pageable pageable = PageRequest.of(page, size, createSort(sort));
        SearchQuery searchQuery = createSearchQueryBuilder(jobSearchRequest)
                .withPageable(pageable)
                .withHighlightFields(new HighlightBuilder.Field("*").fragmentSize(300).numOfFragments(1))
                .withIndices(INDEX_NAME_JOB_ADVERTISEMENT)
                .withTypes(TYPE_JOB_ADVERTISEMENT)
                .build();

        return elasticsearchTemplate.query(searchQuery, response -> {
            AggregatedPage<JobAdvertisementDocument> searchResults = resultsMapper.mapResults(response, JobAdvertisementDocument.class, pageable);
            SearchHits searchHits = response.getHits();
            Iterator<SearchHit> searchHitIterator = searchHits.iterator();

            List<JobAdvertisementDto> highLigtedResults = searchResults.getContent().stream()
                    .map(JobAdvertisementDocument::getJobAdvertisement)
                    .map(JobAdvertisementDto::toDto)
                    .map(jobAdvertisementDto -> highlightFields(jobAdvertisementDto, searchHitIterator.next().getHighlightFields()))
                    .collect(Collectors.toList());

            return new AggregatedPageImpl<>(highLigtedResults, pageable, searchHits.totalHits, searchResults.getAggregations(), searchResults.getScrollId());
        });
    }

    public Page<JobAdvertisementDto> searchPeaJobAdvertisements(
            PeaJobAdvertisementSearchRequest searchRequest,
            Pageable pageable) {

        SearchQuery query = createPeaSearchQueryBuilder(searchRequest)
                .withPageable(pageable)
                .build();

        return jobAdvertisementElasticsearchRepository.search(query)
                .map(JobAdvertisementDocument::getJobAdvertisement)
                .map(JobAdvertisementDto::toDto);
    }

    private NativeSearchQueryBuilder createPeaSearchQueryBuilder(PeaJobAdvertisementSearchRequest searchRequest) {
        QueryBuilder filter = mustAll(
                titleFilter(searchRequest),
                publicationStartDatePeaFilter(searchRequest),
                companyFilter(searchRequest.getCompanyName())
        );

        return new NativeSearchQueryBuilder()
                .withFilter(filter);
    }

    private BoolQueryBuilder titleFilter(PeaJobAdvertisementSearchRequest searchRequest) {
        BoolQueryBuilder query = boolQuery();

        if (isNotBlank(searchRequest.getJobTitle())) {
            query.must(matchQuery(PATH_TITLE, searchRequest.getJobTitle()));
        }

        return query;
    }

    private BoolQueryBuilder publicationStartDatePeaFilter(PeaJobAdvertisementSearchRequest searchRequest) {
        BoolQueryBuilder query = boolQuery();
        if (searchRequest.getOnlineSinceDays() == null) {
            return query;
        }

        String publicationStartDate = String.format("now-%sd/d", searchRequest.getOnlineSinceDays());
        return query.must(rangeQuery(PATH_PUBLICATION_START_DATE).gte(publicationStartDate));
    }

    public long count(JobAdvertisementSearchRequest jobSearchRequest) {
        SearchQuery countQuery = createSearchQueryBuilder(jobSearchRequest).build();
        return elasticsearchTemplate.count(countQuery, JobAdvertisementDocument.class);
    }

    private Sort createSort(SearchSort sort) {
        switch (sort) {
            case date_asc:
                return Sort.by(Sort.Order.asc(PATH_PUBLICATION_START_DATE));
            case date_desc:
                return Sort.by(Sort.Order.desc(PATH_PUBLICATION_START_DATE));
            default:
                return Sort.unsorted();
        }
    }

    private NativeSearchQueryBuilder createSearchQueryBuilder(JobAdvertisementSearchRequest jobSearchRequest) {
        return new NativeSearchQueryBuilder()
                .withQuery(createQuery(jobSearchRequest))
                .withFilter(createFilter(jobSearchRequest));
    }

    private QueryBuilder createQuery(JobAdvertisementSearchRequest jobSearchRequest) {
        if (isEmpty(jobSearchRequest.getKeywords()) && isEmpty(jobSearchRequest.getProfessionCodes())) {
            return matchAllQuery();
        } else {
            return mustAll(createKeywordQuery(jobSearchRequest), createOccupationQuery(jobSearchRequest));
        }
    }

    private BoolQueryBuilder createOccupationQuery(JobAdvertisementSearchRequest jobSearchRequest) {
        if (isNotEmpty(jobSearchRequest.getProfessionCodes())) {
            return Stream.of(jobSearchRequest.getProfessionCodes())
                    .map(this::createOccupationCodeQuery)
                    .reduce(boolQuery(), BoolQueryBuilder::should);
        } else {
            return boolQuery();
        }
    }


    private BoolQueryBuilder createOccupationCodeQuery(ProfessionCode code) {
        final String path;
        switch (code.getType()) {
            case AVAM:
                path = PATH_OCCUPATIONS_AVAM_OCCUPATION_CODE;
                break;
            case BFS:
                path = PATH_OCCUPATIONS_BFS_CODE;
                break;
            case SBN3:
                path = PATH_OCCUPATIONS_SBN3_CODE;
                break;
            case SBN5:
                path = PATH_OCCUPATIONS_SBN5_CODE;
                break;
            case X28:
                path = PATH_X28_CODE;
                break;
            default:
                path = null;
                break;
        }

        return Objects.nonNull(path)
                ? boolQuery().must(termQuery(path, code.getValue()))
                : boolQuery();
    }

    @SuppressWarnings("unchecked")
    private BoolQueryBuilder createKeywordQuery(JobAdvertisementSearchRequest jobSearchRequest) {
        BoolQueryBuilder keywordQuery = boolQuery();

        if (isNotEmpty(jobSearchRequest.getKeywords())) {
            Stream.of(jobSearchRequest.getKeywords())
                    .flatMap(keyword -> keyword.startsWith("*")
                            ? Stream.of(termQuery(PATH_SOURCE_SYSTEM, keyword.substring(1).toUpperCase()).boost(2f))
                            : Stream.of
                            (
                                    multiMatchQuery(keyword, PATH_DESCRIPTION)
                                            .field(PATH_TITLE, 1.5f)
                                            .type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX),
                                    termQuery(PATH_AVAM_JOB_ID, keyword).boost(10f),
                                    termQuery(PATH_EGOV_JOB_ID, keyword).boost(10f)
                            )
                    )
                    .forEach(keywordQuery::should);

            String allKeywords = Stream.of(jobSearchRequest.getKeywords())
                    .filter(keyword -> !keyword.startsWith("*"))
                    .collect(Collectors.joining(" "));

            if (isNotBlank(allKeywords)) {
                keywordQuery.should(multiMatchQuery(allKeywords, PATH_DESCRIPTION, PATH_TITLE)
                        .boost(2f)
                        .operator(Operator.AND));
            }
        }

        return keywordQuery;
    }

    private QueryBuilder createFilter(JobAdvertisementSearchRequest jobSearchRequest) {
        return mustAll(
                visibilityFilter(),
                restrictedJobsFilter(jobSearchRequest),
                publicationStartDateFilter(jobSearchRequest),
                localityFilter(jobSearchRequest),
                workingTimeFilter(jobSearchRequest),
                contractTypeFilter(jobSearchRequest),
                companyFilter(jobSearchRequest.getCompanyName()));
    }

    private BoolQueryBuilder publicationStartDateFilter(JobAdvertisementSearchRequest jobSearchRequest) {
        int onlineSince = Optional.ofNullable(jobSearchRequest.getOnlineSince()).orElse(ONLINE_SINCE_DAYS);
        String publicationStartDate = String.format("now-%sd/d", onlineSince);

        // todo filter by the last action
        return boolQuery().must(rangeQuery(PATH_PUBLICATION_START_DATE).gte(publicationStartDate));
    }

    private BoolQueryBuilder companyFilter(String companyName) {
        BoolQueryBuilder companyFilter = boolQuery();

        if (isNotBlank(companyName)) {
            companyFilter.must(matchPhraseQuery(PATH_COMPANY_NAME, companyName));
        }

        return companyFilter;
    }

    private BoolQueryBuilder contractTypeFilter(JobAdvertisementSearchRequest jobSearchRequest) {
        BoolQueryBuilder contractTypeFilter = boolQuery();

        if (jobSearchRequest.isPermanent() != null) {
            contractTypeFilter.must(termsQuery(PATH_PERMANENT, jobSearchRequest.isPermanent()));
        }

        return contractTypeFilter;
    }

    private BoolQueryBuilder localityFilter(JobAdvertisementSearchRequest jobSearchRequest) {
        BoolQueryBuilder localityFilter = boolQuery();

        if (isNotEmpty(jobSearchRequest.getCantonCodes())) {
            localityFilter.should(termsQuery(PATH_LOCATION_CANTON_CODE, jobSearchRequest.getCantonCodes()));
        }
        if (isNotEmpty(jobSearchRequest.getRegionCodes())) {
            localityFilter.should(termsQuery(PATH_LOCATION_REGION_CODE, jobSearchRequest.getRegionCodes()));
        }
        if (isNotEmpty(jobSearchRequest.getCommunalCodes())) {
            localityFilter.should(termsQuery(PATH_LOCATION_COMMUNAL_CODE, jobSearchRequest.getCommunalCodes()));
        }

        return localityFilter;
    }

    private BoolQueryBuilder visibilityFilter() {
        BoolQueryBuilder visibilityFilter = boolQuery();

        if (this.currentUserContext.hasRole(Role.JOBSEEKER_CLIENT)) {
            visibilityFilter.must(termsQuery(PATH_STATUS, PUBLISHED_RESTRICTED.toString(), PUBLISHED_PUBLIC.toString()));
        } else {
            visibilityFilter.must(termsQuery(PATH_STATUS, PUBLISHED_PUBLIC.toString()));
        }

        return visibilityFilter;
    }

    private BoolQueryBuilder restrictedJobsFilter(JobAdvertisementSearchRequest jobSearchRequest) {
        BoolQueryBuilder restrictedJobsFilter = boolQuery();

        if (this.currentUserContext.hasRole(Role.JOBSEEKER_CLIENT) && jobSearchRequest.getDisplayRestricted() != null) {
            restrictedJobsFilter.must(termsQuery(PATH_REPORTING_OBLIGATION, jobSearchRequest.getDisplayRestricted()));
        }

        return restrictedJobsFilter;
    }

    private BoolQueryBuilder workingTimeFilter(JobAdvertisementSearchRequest jobSearchRequest) {
        BoolQueryBuilder workingTimeFilter = boolQuery();

        if (jobSearchRequest.getWorkloadPercentageMin() != null) {
            workingTimeFilter.must(rangeQuery(PATH_WORKLOAD_PERCENTAGE_MAX).gte(jobSearchRequest.getWorkloadPercentageMin()));
        }

        if (jobSearchRequest.getWorkloadPercentageMax() != null) {
            workingTimeFilter.must(rangeQuery(PATH_WORKLOAD_TIME_PERCENTAGE_MIN).lte(jobSearchRequest.getWorkloadPercentageMax()));
        }

        return workingTimeFilter;
    }


    private static BoolQueryBuilder mustAll(BoolQueryBuilder... queryBuilders) {
        return Stream.of(queryBuilders)
                .filter(BoolQueryBuilder::hasClauses)
                .reduce(boolQuery(), BoolQueryBuilder::must);
    }

    private static JobAdvertisementDto highlightFields(JobAdvertisementDto jobAdvertisementDto, Map<String, HighlightField> highlightFieldMap) {
        for (JobDescriptionDto jobDescriptionDto : jobAdvertisementDto.getJobContent().getJobDescriptions()) {

            if (highlightFieldMap.containsKey(PATH_TITLE)) {
                jobDescriptionDto.setTitle(highlightFieldMap.get(PATH_TITLE).getFragments()[0].toString());
            }

            if (highlightFieldMap.containsKey(PATH_DESCRIPTION)) {
                jobDescriptionDto.setDescription(highlightFieldMap.get(PATH_DESCRIPTION).getFragments()[0].toString());
            }

        }

        return jobAdvertisementDto;
    }
}