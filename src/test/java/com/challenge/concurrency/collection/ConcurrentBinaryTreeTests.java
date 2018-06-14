package com.challenge.concurrency.collection;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrentBinaryTreeTests {

	ExecutorService executorService;

	ConcurrentBinaryTree<Integer> tree;

	Callable<List<Pair<Integer,Long>>> callableWriter;
	Callable<List<Pair<Integer,Long>>> callableReader;

	ConcurrentLinkedQueue<Pair<Integer,Long>> sizeMetrics;

	private static int READER_COUNT = 100;

	private static int LOOP_COUNT = 1000;

	Set<Callable<List<Pair<Integer,Long>>>> callables;

	@Before
	public void init(){

		sizeMetrics = new ConcurrentLinkedQueue<Pair<Integer,Long>>();

		tree = new ConcurrentBinaryTree<Integer>();

		executorService = Executors.newFixedThreadPool(READER_COUNT + 1);


		callables = new HashSet<Callable<List<Pair<Integer,Long>>>>();

		callables.add(
				new Callable<List<Pair<Integer, Long>>>() {
					@Override
					public List<Pair<Integer, Long>> call() throws Exception {
						List<Pair<Integer,Long>> writeMetrics = new ArrayList<>();

						try {
							for(int i = 0 ; i < LOOP_COUNT; i++) {
								tree.add(i);
								writeMetrics.add(new Pair<>(tree.size(),System.nanoTime()));
								TimeUnit.MILLISECONDS.sleep(2);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return writeMetrics;					}
				}
		);

		for( int i = 0; i < READER_COUNT; i++){
			callables.add(
					new Callable<List<Pair<Integer, Long>>>() {
						@Override
						public List<Pair<Integer, Long>> call() throws Exception {
							List<Pair<Integer,Long>> readMetrics = new ArrayList<>();

							try {

								for(int i = 0 ; i < LOOP_COUNT; i++) {
									tree.size();
									readMetrics.add(new Pair<>(tree.size(),System.nanoTime()));
									TimeUnit.MILLISECONDS.sleep(2);
								}
							} catch (InterruptedException e) {
							}

							return readMetrics;
						}
					}
			);
		}


	}


	@Test
	public void add() throws InterruptedException, ExecutionException {


		List<Pair<Integer,Long>> totalMetrics = new ArrayList<>();
		List<Future<List<Pair<Integer,Long>>>> futures = executorService.invokeAll(callables);

		for (Future<List<Pair<Integer,Long>>> future : futures)
		{
			totalMetrics.addAll(future.get());
		}
		executorService.shutdown();


		Comparator<Pair<Integer,Long>> byTime
				= (Pair<Integer,Long> point1, Pair<Integer,Long> point2) -> point1.getValue().compareTo(point2.getValue());

		Comparator<Pair<Integer,Long>> byValue
				= (Pair<Integer,Long> point1, Pair<Integer,Long> point2) -> point1.getKey().compareTo(point2.getKey());

		totalMetrics.sort(byTime);

		assertThat(totalMetrics).isSortedAccordingTo(byValue);
	}

}
