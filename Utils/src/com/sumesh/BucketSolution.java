package com.sumesh;

import java.util.HashMap;
import java.util.Map;

public class BucketSolution
{
	public static void main(String[] args)
	{
		getVolumeAtIndex(1000, 15, 4);
	}

	static void getVolumeAtIndex(float volume, float maxCapacity, int searchIndex)
	{
		Map<Integer, Bucket> buckets = new HashMap<Integer, Bucket>();
		if (volume > 0 && searchIndex > 0)
		{
			Bucket root = new BucketSolution().new Bucket();
			root.setIndex(1);
			root.setLevel(1);

			buckets.put(1, root);

			fillBuckets(volume, maxCapacity, root, buckets);
			Bucket searchBucket = buckets.get(searchIndex);

			if (null == searchBucket)
			{
				System.out.println("Bucket " + searchIndex + " has volume 0");
			}
			else
			{
				System.out.println("Bucket " + searchIndex + " has volume " + searchBucket.getCurrrentVolume());
			}

		}
		else
			System.out.println("Bucket " + searchIndex + " has volume 0");

		float sum = 0;
		for (Bucket bucket : buckets.values())
		{
			sum = sum + bucket.getCurrrentVolume();
			System.out.println("Bucket at index " + bucket.getIndex() + " has volume " + bucket.getCurrrentVolume());
		}

		System.out.println("Total : " + sum);

	}

	static void fillBuckets(float volume, float maxCapacity, Bucket root, Map<Integer, Bucket> buckets)
	{
		if (volume > 0)
		{
			if (volume > maxCapacity)
			{
				fillChildren(maxCapacity, root, buckets, volume);
			}
			else
			{
				root.setVolume(volume);
			}
		}
	}

	private static void fillChildren(float maxCapacity, Bucket root, Map<Integer, Bucket> buckets, float volume)
	{
		// Assumes that when calling this method volume is more than maxCapacity
		root.setVolume(maxCapacity);

		float remainingVolume = volume - maxCapacity;
		float volumeLeft = remainingVolume / 2;
		float volumeRight = remainingVolume / 2;

		// Create children to evenly distribute remainingVolume
		int nextLeftNodeIndex = root.getLevel() + root.getIndex();
		int nextRightNodeIndex = root.getLevel() + root.getIndex() + 1;

		Bucket leftChild;
		if (buckets.get(nextLeftNodeIndex) != null)
		{
			leftChild = buckets.get(nextLeftNodeIndex);
			volumeLeft = volumeLeft + leftChild.getCurrrentVolume();
		}
		else
		{
			leftChild = new BucketSolution().new Bucket();
			leftChild.setIndex(nextLeftNodeIndex);
			leftChild.setLevel(root.getLevel() + 1);
			buckets.put(leftChild.getIndex(), leftChild);
		}

		Bucket rightChild;
		if (buckets.get(nextRightNodeIndex) != null)
		{
			rightChild = buckets.get(nextRightNodeIndex);
			volumeRight = volumeRight + rightChild.getCurrrentVolume();
		}
		else
		{
			rightChild = new BucketSolution().new Bucket();
			rightChild.setIndex(nextRightNodeIndex);
			rightChild.setLevel(root.getLevel() + 1);
			buckets.put(rightChild.getIndex(), rightChild);
		}

		root.setLeft(leftChild);
		root.setRight(rightChild);

		if (volumeLeft > maxCapacity)
			// fill left tree
			fillBuckets(volumeLeft, maxCapacity, leftChild, buckets);
		else
			leftChild.setVolume(volumeLeft);

		if (volumeRight > maxCapacity)
			// fill right tree
			fillBuckets(volumeRight, maxCapacity, rightChild, buckets);
		else
			rightChild.setVolume(volumeRight);

	}

	class Bucket
	{
		private int		index;
		private Bucket	leftChild;
		private Bucket	rightChild;
		private float	currentVolume;
		private int		level;

		public void setLevel(int level)
		{
			this.level = level;
		}

		public int getLevel()
		{
			return this.level;
		}

		public void setVolume(float volume)
		{
			this.currentVolume = volume;
		}

		public float getCurrrentVolume()
		{
			return this.currentVolume;
		}

		public void setIndex(int index)
		{
			this.index = index;
		}

		public int getIndex()
		{
			return this.index;
		}

		public Bucket getLeft()
		{
			return this.leftChild;
		}

		public Bucket getRight()
		{
			return this.rightChild;
		}

		public void setLeft(Bucket left)
		{
			this.leftChild = left;
		}

		public void setRight(Bucket right)
		{
			this.rightChild = right;
		}
	}
}
