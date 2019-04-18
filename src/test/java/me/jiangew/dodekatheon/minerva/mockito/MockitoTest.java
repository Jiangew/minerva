package me.jiangew.dodekatheon.minerva.mockito;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jiangew
 * @desc
 */
public class MockitoTest {

  @Before
  public void setUp() {

  }

  class ListOfTwoElements implements ArgumentMatcher<List> {
    public boolean matches(List list) {
      return list.size() == 2;
    }

    public String toString() {
      // printed in verification errors
      return "[list of 2 element]";
    }
  }

  @Test
  public void testList() {
    // mock concrete interface
    List mockedList = mock(List.class);

    mockedList.add("one");
    mockedList.clear();

    verify(mockedList).add("one");
    verify(mockedList).clear();

    when(mockedList.addAll(argThat(new ListOfTwoElements()))).thenReturn(true);
    mockedList.addAll(Arrays.asList("one", "two"));

    verify(mockedList).addAll(argThat(new ListOfTwoElements()));
  }

  @Test
  public void testStub() {
    // You can mock concrete classes, not just interfaces
    LinkedList mockedList = mock(LinkedList.class);

    // stubbing
    when(mockedList.get(0)).thenReturn("jiangew");
    when(mockedList.get(1)).thenReturn(new RuntimeException());

    System.out.println(mockedList.get(0));
    System.out.println(mockedList.get(1));
    System.out.println(mockedList.get(100));

    verify(mockedList).get(0);
  }

  @Test
  public void testInvokeTimes() {
    LinkedList mockedList = mock(LinkedList.class);

    mockedList.add("once");
    mockedList.add("twice");
    mockedList.add("twice");
    mockedList.add("three times");
    mockedList.add("three times");
    mockedList.add("three times");

    // following two verifications work exactly the same - times(1) is used by default
    verify(mockedList).add("once");
    verify(mockedList, times(1)).add("once");

    // exact number of invocations verification
    verify(mockedList, times(2)).add("twice");
    verify(mockedList, times(3)).add("three times");

    // verification using nerver(). is an alias to times(0)
    verify(mockedList, never()).add("never happened");

    verify(mockedList, atLeastOnce()).add("three times");
    verify(mockedList, atLeast(2)).add("three times");
    verify(mockedList, atMost(5)).add("three times");

    doThrow(new RuntimeException()).when(mockedList).clear();
    mockedList.clear();
  }

  @Test
  public void testSequence() {
    List singleMock = mock(List.class);
    singleMock.add("was added first");
    singleMock.add("was added second");

    InOrder inOrder = inOrder(singleMock);
    inOrder.verify(singleMock).add("was added first");
    inOrder.verify(singleMock).add("was added second");

    List firstMock = mock(List.class);
    List secondMock = mock(List.class);
    firstMock.add("was added first");
    secondMock.add("was added second");

    InOrder inOrder2 = inOrder(firstMock, secondMock);
    inOrder2.verify(firstMock).add("was added first");
    inOrder2.verify(secondMock).add("was added second");
  }

  @Test
  public void testStubCallback() {
    List list = mock(List.class);

    // stubbing with callbacks
    when(list.add(anyString())).thenAnswer(new Answer() {
      public Object answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        Object mock = invocation.getMock();
        return "called with arguments: " + Arrays.toString(args);
      }
    });
    // following prints "called with arguments: [foo]"
    System.out.println(list.add("foo"));
  }

}
