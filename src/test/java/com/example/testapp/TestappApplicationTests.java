package com.example.testapp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestappApplicationTests {
	@Autowired
	private MainService mainService;

	private String parseId(String user) {
        String[] l = user.split(",");
        for (String s: l) {
            int start = s.indexOf("id=");
            if (start > 0)
                return s.substring(start + "id=".length());
        }
	    return null;
    }

	@Before
    public void clean() {
	    String[] list = (String[])mainService.exec("l", "size:20000");
	    List<String> ids = new ArrayList<>();
        for (String user: list) {
            String id = parseId(user);
            if (id != null)
                ids.add(id);
        }

        for (String id: ids) {
            mainService.exec("d", "id:" + id);
        }
    }

	@Test
	public void testCreate() {
	    String user = (String)mainService.exec("c", "username:aaa", "name:bbb", "email:a@b.com", "password:ccc");
	    String id = parseId(user);
        Assert.assertEquals(user,
                "{id=" + id + ", name=bbb, username=aaa, email=a@b.com, password=ccc}");
	}

	@Test
    public void testRead() {
        String user = (String)mainService.exec("c", "username:aaa", "name:bbb", "email:a@b.com", "password:ccc");
        String id = parseId(user);
        String r = (String)mainService.exec("r", "id:" + id);
        Assert.assertEquals(r, user);
    }

    @Test
    public void testUpdate() {
        String user = (String)mainService.exec("c", "username:aaa", "name:bbb", "email:a@b.com", "password:ccc");
	    String id = parseId(user);
	    String user2 = (String)mainService.exec("u", "id:" + id, "username:xxx", "email:x@y.z");
        Assert.assertEquals(user2,
                "{id=" + id + ", name=bbb, username=xxx, email=x@y.z, password=ccc}");
    }

    @Test
    public void testDelete() {
        String user = (String)mainService.exec("c", "username:aaa", "name:bbb", "email:a@b.com", "password:ccc");
        String id = parseId(user);
        String r = (String)mainService.exec("d", "id:" + id);
        Assert.assertEquals(r, user);
    }

    @Test
    public void testList() {
        String user = (String)mainService.exec("c", "username:aaa", "name:bbb", "email:a@b.com", "password:ccc");
        String user2 = (String)mainService.exec("c", "username:aaa2", "name:bbb2", "email:a2@b.com", "password:ccc");

        String[] list = (String[])mainService.exec("l");
        Assert.assertEquals(list.length, 2);
	    Assert.assertEquals(list[0], user);
        Assert.assertEquals(list[1], user2);

        list = (String[])mainService.exec("l", "size:1");
        Assert.assertEquals(list.length, 1);
        Assert.assertEquals(list[0], user);

        list = (String[])mainService.exec("l", "page:1", "size:1");
        Assert.assertEquals(list.length, 1);
        Assert.assertEquals(list[0], user2);
    }

    @Test
    @Async
    public void testPerformance() {
        long start = System.currentTimeMillis();

	    CompletableFuture[] cfs = new CompletableFuture[10000];
	    for (int i = 0; i < cfs.length; i ++) {
            CompletableFuture<Object> cf = CompletableFuture.completedFuture(
                    mainService.exec("c", "username:un" + i, "name:n" + i, "email:a@b.com" + i, "password:ccc"));
            cfs[i] = cf;
        }

        CompletableFuture.allOf(cfs).join();

        System.out.println(System.currentTimeMillis() - start);
    }
}
