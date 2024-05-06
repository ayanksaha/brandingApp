package com.lb.brandingApp.product.service;

import com.lb.brandingApp.auth.data.entities.Team;
import com.lb.brandingApp.auth.repository.TeamRepository;
import com.lb.brandingApp.category.data.entities.Category;
import com.lb.brandingApp.category.data.models.request.WorkflowItemRequestDto;
import com.lb.brandingApp.category.data.models.response.WorkflowItemResponseDto;
import com.lb.brandingApp.category.repository.CategoryRepository;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.TimePeriod;
import com.lb.brandingApp.common.data.entities.WorkflowItem;
import com.lb.brandingApp.common.data.models.request.TimePeriodRequestDto;
import com.lb.brandingApp.common.data.models.response.TimePeriodResponseDto;
import com.lb.brandingApp.common.mapper.CommonMapper;
import com.lb.brandingApp.common.repository.AmountRepository;
import com.lb.brandingApp.common.repository.TimePeriodRepository;
import com.lb.brandingApp.common.repository.WorkflowItemRepository;
import com.lb.brandingApp.product.data.entities.ProductConfig;
import com.lb.brandingApp.product.data.models.request.ProductConfigRequestDto;
import com.lb.brandingApp.product.data.models.response.ProductConfigResponseDto;
import com.lb.brandingApp.product.repository.ProductConfigRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.lb.brandingApp.app.constants.ApplicationConstants.*;

//Dropdown values will not be paginated.
@Service
@Transactional
@Slf4j
public class ProductConfigService {

    @Autowired
    private ProductConfigRepository productConfigRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private WorkflowItemRepository workflowItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TimePeriodRepository timePeriodRepository;

    @Autowired
    private AmountRepository amountRepository;

    @Autowired
    private CommonMapper commonMapper;

    public List<ProductConfigResponseDto> getAllProductConfigs(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        return productConfigRepository.findAllByCategory(category).stream().map(
                productConfig -> ProductConfigResponseDto.builder()
                        .name(productConfig.getName())
                        .id(productConfig.getId())
                        .unitAmount(productConfig.getAmount())
                        .validity(Objects.nonNull(productConfig.getValidity()) ?
                                TimePeriodResponseDto.builder()
                                        .id(productConfig.getValidity().getId())
                                        .value(productConfig.getValidity().getValue())
                                        .unit(productConfig.getValidity().getUnit())
                                        .build()
                                : null)
                        .workflow(getWorkflow(productConfig))
                        .build()
        ).sorted(Comparator.comparing(ProductConfigResponseDto::getName)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addProductConfig(@NonNull Long categoryId, ProductConfigRequestDto request) {
        ProductConfig productConfig = new ProductConfig();
        productConfig.setName(request.productName());

        Amount amount = commonMapper.mapAmount(request.amount().value());
        amountRepository.save(amount);
        productConfig.setAmount(amount);
        TimePeriodRequestDto validityRequest = request.validity();
        if (Objects.nonNull(validityRequest)) {
            TimePeriod validity = new TimePeriod();
            validity.setUnit(validityRequest.unit());
            validity.setValue(validityRequest.value());
            timePeriodRepository.save(validity);
            productConfig.setValidity(validity);
        }

        Set<WorkflowItem> workflow = new LinkedHashSet<>();
        for (WorkflowItemRequestDto workflowItem : request.workflow()) {
            Team team = teamRepository.findById(workflowItem.id())
                    .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
            WorkflowItem item = new WorkflowItem();
            item.setTeam(team);
            item.setSequence(workflowItem.order());
            workflow.add(item);
        }
        workflowItemRepository.saveAll(workflow);
        productConfig.setWorkflow(workflow);
        productConfigRepository.save(productConfig);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        Set<ProductConfig> productConfigs = category.getProducts();
        productConfigs.add(productConfig);
        category.setProducts(productConfigs);
        categoryRepository.save(category);
        log.info("Successful!");
    }

    public void updateProductConfig(Long productId, @NonNull Long categoryId, ProductConfigRequestDto request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND));
        ProductConfig product = productConfigRepository.findByIdAndCategory(productId, category)
                .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND));
        String productName = request.productName();
        if(Objects.nonNull(productName)) {
            product.setName(productName);
        }
        if (Objects.nonNull(request.amount())) {
            Amount amount = product.getAmount();
            amount.setValue(request.amount().value());
            product.setAmount(amount);
        }
        if(!product.getAllotments().isEmpty() && !request.workflow().isEmpty()) {
            throw new RuntimeException(WORKFLOW_INCONSISTENT_ERROR);
        }
        if(!request.workflow().isEmpty()) {
            Set<WorkflowItem> workflow = new LinkedHashSet<>();

            //Setting workflow for newer tasks only
            for (WorkflowItemRequestDto workflowItem : request.workflow()) {
                Team team = teamRepository.findById(workflowItem.id())
                        .orElseThrow(() -> new RuntimeException(TEAM_NOT_FOUND));
                List<WorkflowItem> workflowItemInDb = workflowItemRepository.findAllByProductConfigAndTeam(product, team);
                WorkflowItem item;
                if (workflowItemInDb.isEmpty()) {
                    workflowItemInDb = workflowItemRepository.findAllByProductConfigAndTeam(null, team);
                }
                if (workflowItemInDb.isEmpty()) {
                    item = new WorkflowItem();
                    item.setTeam(team);
                    item.setSequence(workflowItem.order());
                    workflowItemRepository.save(item);
                } else {
                    item = workflowItemInDb.get(0);
                }
                workflow.add(item);
            }
            product.setWorkflow(workflow);
        }
        productConfigRepository.save(product);
    }

    LinkedHashSet<WorkflowItemResponseDto> getWorkflow(ProductConfig productConfig) {
        LinkedHashSet<WorkflowItemResponseDto> workflowInResponse = new LinkedHashSet<>();
        List<WorkflowItem> workflowInDb = new ArrayList<>(productConfig.getWorkflow());
        workflowInDb.sort(Comparator.comparing(WorkflowItem::getSequence));
        for (WorkflowItem item : workflowInDb) {
            Team team = item.getTeam();
            workflowInResponse.add(WorkflowItemResponseDto.builder()
                    .teamId(team.getId())
                    .name(team.getDescription().name())
                    .description(team.getDescription().description())
                    .order(item.getSequence())
                    .build());
        }
        return workflowInResponse;
    }
}
