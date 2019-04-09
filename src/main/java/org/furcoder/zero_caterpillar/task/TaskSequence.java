package org.furcoder.zero_caterpillar.task;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskSequence implements Task.Cancelable
{
	List<Task> tasks = new ArrayList<>();
	Iterator<Task> iterator = null;

	Runnable runningTask = null;;
	boolean canceled;


	@Override
	public float completionRate()
	{
		return (float) tasks.stream().mapToDouble(t -> t.completionRate()).filter(r -> !Double.isNaN(r)).average().getAsDouble();
	}

	@Override
	public void cancel()
	{
		canceled = true;
		if (runningTask instanceof Cancelable) ((Cancelable) runningTask).cancel();
	}

	@Override
	public void run()
	{
		iterator = tasks.iterator();
		while (iterator.hasNext() && !canceled)
		{
			runningTask = iterator.next();
			runningTask.run();
		}
		runningTask = null;
	}

	public boolean isRunning()
	{
		return runningTask != null;
	}

	public void addTask(Task task)
	{
		tasks.add(task);
	}
}
