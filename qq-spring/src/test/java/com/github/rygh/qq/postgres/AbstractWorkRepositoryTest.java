package com.github.rygh.qq.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.domain.WorkState;
import com.github.rygh.qq.repositories.WorkRepository;

public abstract class AbstractWorkRepositoryTest {

	protected abstract WorkRepository getInstance();

	protected Set<Long> createLotsOfWork(String consumer, int count) {
		return Stream.generate(() -> consumer)
			.limit(count)
			.map(this::createWork)
			.map(Work::getId)
			.collect(Collectors.toSet());
	}
	
	protected Work createWork(String consumer) {
		return createWork(consumer, WorkState.READY);
	}
	
    protected Work createWork(String consumer, WorkState state) {
    	return getInstance().store(new Work(LocalDateTime.now(), new EntityId(UUID.randomUUID().toString(), Object.class), consumer).setState(state));
    }
    
    @Test
    public void shouldOnlyClaimForGivenPool() {
    	createWork("TestQueue-first");
    	createWork("TestQueue-first");
    	createWork("SecondQueue-second");
    	
    	assertEquals(2, getInstance().claimNextReadyForPool(10, "first").count());
    	assertEquals(1, getInstance().claimNextReadyForPool(10, "second").count());
    }
    
    @Test
    public void shouldOnlyClaimForActiveConsumers() {
    	createWork("DisabledQueue-first");
    	assertEquals(0, getInstance().claimNextReadyForPool(10, "first").count());
    }
    
    @Test
    public void shouldOnlyClaimReadyWork() {
    	createWork("TestQueue-first", WorkState.PROCESSING);
    	createWork("TestQueue-first");
    	
    	assertEquals(1, getInstance().claimNextReadyForPool(10, "first").count());
    }
    
    @Test
    public void shouldUpdateWorkWithMillisecondTimestamps() {
    	Work original = createWork("TestQueue-first");

    	LocalDateTime startedTime = LocalDateTime.of(2018, 5, 17, 22, 5, 20, (int)TimeUnit.MILLISECONDS.toNanos(75));
    	LocalDateTime completedTime = LocalDateTime.of(2018, 5, 17, 22, 10, 34, (int)TimeUnit.MILLISECONDS.toNanos(134));
    	
    	getInstance().update(original.setStartedTime(startedTime).setCompletedTime(completedTime).setState(WorkState.COMPLETED));
    	
    	Work updated = getInstance().getById(original.getId()).get();
    	
    	assertEquals("Expects updated value with millisecond precision", startedTime, updated.getStartedTime());
    	assertEquals("Expects updated value with millisecond precision", completedTime, updated.getCompletedTime());
    	assertTrue("Expectes new state to be stored", updated.is(WorkState.COMPLETED));
    	assertEquals("Repository is expected to update version", 2, updated.getVersion());
    }
    
    @Test
    public void shouldLimitClaimToCount() {
    	Set<Long> createdIds = createLotsOfWork("TestQueue-first", 12);
    	assertEquals(12, createdIds.size());
    	
    	Set<Long> firstClaim = getInstance().claimNextReadyForPool(5, "first").map(Work::getId).collect(Collectors.toSet());
    	assertEquals("Should fetch first 5 of 12", 5, firstClaim.size());
    	Set<Long> secondClaim = getInstance().claimNextReadyForPool(5, "first").map(Work::getId).collect(Collectors.toSet());
    	assertEquals("Should fetch next 5 of 12", 5, secondClaim.size());
    	Set<Long> thirdClaim = getInstance().claimNextReadyForPool(5, "first").map(Work::getId).collect(Collectors.toSet());
    	assertEquals("Should fetch last 2 of 12", 2, thirdClaim.size());
    	assertEquals("None left", 0, getInstance().claimNextReadyForPool(5, "first").count());
    	
    	Set<Long> allClaimed = new HashSet<>();
    	allClaimed.addAll(firstClaim);
    	allClaimed.addAll(secondClaim);
    	allClaimed.addAll(thirdClaim);
    	
    	assertEquals("Verify that all work has been fetched", createdIds, allClaimed);
    }
    
    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotCreateWorkForUnconfiguredConsumer() {
    	createWork("DoesNotExistQueue");
    }
    
    @Test(expected = OptimisticLockingFailureException.class)
    public void shouldFailOnDirtyWrite() {
    	Work originalWork = createWork("TestQueue-first");
    	
    	Work firstRead = getInstance().getById(originalWork.getId()).get();
    	Work secondRead = getInstance().getById(originalWork.getId()).get();
    	assertEquals("Verify version same for both reads", firstRead.getVersion(), secondRead.getVersion());
    	
    	getInstance().update(firstRead.setState(WorkState.COMPLETED));
    	getInstance().update(secondRead.setState(WorkState.FAILED));
    }
}
