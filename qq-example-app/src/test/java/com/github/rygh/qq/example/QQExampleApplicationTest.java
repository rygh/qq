package com.github.rygh.qq.example;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.rygh.qq.domain.WorkState;
import com.github.rygh.qq.example.job.Job;
import com.github.rygh.qq.example.rock.RockSpec;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class QQExampleApplicationTest {

	@Autowired
	private TestRestTemplate rest;
	
	@Test(timeout = 10_0000)
	public void smokeTest() {
		rest.postForLocation("/api/rocks", new RockSpec().setName("HELP"));
		while (!areWeThereYet()) {
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private boolean areWeThereYet() {
		Job[] jobs = rest.getForObject("/api/jobs", Job[].class);
		return jobs.length == 5 && Arrays.stream(jobs).allMatch(j -> j.getState() == WorkState.COMPLETED);
 	}

}
