package norainu;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("aaaaa");
		Test t = new Test() {
			@Override
			public String getGreeting(String name) {
				if (name.isEmpty()) {
					return "Hello nobody!";
				}
				return "Hello " + name + "!";

			}
		};
		System.out.println(t.getGreeting("aaaa"));
		// ラムダ
		Test t2 = (name) -> {
			if (name.isEmpty()) {
				return "h n";
			}
			return "h " + name;
		};
		System.out.println(t2.getGreeting("nnnn"));
		TestDefaultMethod tf = new TestDefaultMethod() {
		};
		System.out.println(tf.getName());
		TestDefaultMethod tf2 = new TestDefaultMethodImpl();
		System.out.println(tf2.getName());

		// nullable
		System.out.println(getN("4").orElse("default").toUpperCase());

		// 型推論
		var h = "あああああ";
		System.out.println(h);

		// date and time api
		// 従来
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.get(Calendar.YEAR));

		System.out.println(Year.now());
		LocalDateTime d = LocalDateTime.now();
		//年月日時分秒を指定
		LocalDateTime d1 = LocalDateTime.of(2015, 12, 15, 23, 30, 59);

		System.out.println(d.getYear());
		System.out.println(d.getMonth());
		System.out.println(d.getDayOfMonth());
		System.out.println(d.getHour());
		System.out.println(d.getMinute());
		System.out.println(d.getSecond());
		System.out.println(d.getNano());
		System.out.println(d.get(ChronoField.YEAR));

		// Stream
		// 従来
		List<String> list = Arrays.asList("C", "C++", "Java", "Scala", "Ruby");

		int count = 0;
		for (String elem : list) {
			if (elem.startsWith("C")) {
				count += elem.length();
			}
		}
		System.out.println("count:" + count);
		long count2 = list.stream()
				.filter(s -> s.startsWith("C"))
				.mapToInt(s -> s.length())
				.sum();
		System.out.println("count2:" + count2);

		// Stream with line
		Path path = Paths.get("C:/tmp/a.txt");
		try (Stream<String> lines = Files.lines(path); FileOutputStream out = new FileOutputStream("C:/tmp/a3.txt")) {
			OutputStreamWriter ow = new OutputStreamWriter(out, "UTF-8");
			//			lines.forEach(System.out::println);
			lines.forEach((line -> {
				try {
					ow.write(line + "\n");
				} catch (Exception e) {
					// ここに適切な例外処理
				}
			}));
			ow.flush();
		}

		// 非同期
		ExecutorService es = Executors.newCachedThreadPool();
		CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
			try {
				return heavyWork("job1");
			} catch (Exception e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
				return "job1 fail";
			}
		}, es);

		cf.whenComplete((ret, ex) -> {
			if (ex == null) {
				//成功
				System.out.println("success:" + ret);
			} else {
				System.out.println("exception,fail:" + ex);
			}
			return;
		});

		// 複数待ち合わせ
		List<CompletableFuture<String>> cfs = Arrays.asList(
				CompletableFuture.supplyAsync(() -> {
					try {
						return heavyWork("job1");
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						return "job1 fail";
					}
				}, es),
				CompletableFuture.supplyAsync(() -> {
					try {
						return heavyWork("job2");
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						return "job2 fail";
					}
				}, es),
				CompletableFuture.supplyAsync(() -> {
					try {
						return heavyWork("job3");
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						return "job3 fail";
					}
				}, es));

		CompletableFuture<Void> cf2 = CompletableFuture.allOf(
				cfs.toArray(new CompletableFuture[cfs.size()]));

		cf2.whenComplete((ret, ex) -> {
			if (ex == null) {
				// すべて成功した場合
				String msg = cfs.stream().map(future -> {
					try {
						return future.get();
					} catch (Exception iex) {
						return iex.toString();
					}
				}).collect(Collectors.joining(","));
				System.out.println("result=" + msg);

			} else {
				// いずれかが失敗した場合(いずれか1つの例外のみ取得される)
				System.err.println("err=" + ex);
			}
		});

		// java9
		// try-resourceを外出できるようになった
		Stream<String> lines = Files.lines(path);
		try (lines) {
			lines.forEach(System.out::println);
		}
		// OptionalにifPresentOrElse
		Optional<String> op1 = Optional.ofNullable(null);
		op1.ifPresentOrElse((xxx) -> System.out.println(xxx), () -> System.out.println("nullでっす"));

		{
			// ...While
			// java8
			// arrange
			List<Integer> list2 = Arrays.asList(0, 1, 2, -1, 2);

			// act
			Predicate<Integer> p = (x) -> x <= 1;

			boolean isDirty = false;
			for (Integer x : list2) {
				if (!isDirty && p.negate().test(x))
					isDirty = true;
				if (isDirty)
					System.out.println(x);
			}
			// 2
			// -1
			// 2

			long skipIndex = list2.indexOf(list2.stream().filter(p.negate()).findFirst().get());
			list2.stream().skip(skipIndex).forEach(System.out::println);

			// java9
			// act
			list2.stream().dropWhile(x -> x <= 1).forEach(System.out::println);
			list2.stream().takeWhile(x -> x <= 1).forEach(System.out::println);
		}

	}

	private static String heavyWork(String jobName) throws Exception {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new Exception();
		}
		return jobName;
	}

	private static Optional<String> getN(String key) {
		Map<String, String> dMap = new HashMap<>();
		dMap.put("1", "1111");
		dMap.put("2", "222");
		return Optional.ofNullable(dMap.get(key));
	}
}

// ラムダ系
@FunctionalInterface
interface Test {
	public String getGreeting(String name);
}

// デフォルトメソッド
// っていうかこんなの許したら初心者混乱するのでは？
interface TestDefaultMethod {
	public default String getName() {
		return "s";
	}
}

class TestDefaultMethodImpl implements TestDefaultMethod {

}
