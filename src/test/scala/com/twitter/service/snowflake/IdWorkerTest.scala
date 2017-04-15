package com.twitter.service.snowflake;

import org.junit.Test
import org.junit.Assert._
import org.specs2.mutable.Specification

/**
 * Created by masakir on 2017/04/15.
 */
class IdWorkerTest {
  val workerMask     = 0x000000000001F000L
  val datacenterMask = 0x00000000003E0000L
  val timestampMask  = 0xFFFFFFFFFFC00000L

  class EasyTimeWorker(workerId: Long, datacenterId: Long) extends IdWorker(workerId, datacenterId) {
    var timeMaker = (() => System.currentTimeMillis)
    override def timeGen(): Long = {
      timeMaker()
    }
  }

  class WakingIdWorker(workerId: Long, datacenterId: Long) extends EasyTimeWorker(workerId, datacenterId) {
    var slept = 0
    override def tilNextMillis(lastTimestamp:Long): Long = {
      slept += 1
      super.tilNextMillis(lastTimestamp)
    }
  }

  @Test
  def createId(): Unit = {
    val worker = new IdWorker(1, 1)
    for (i <- 0 until 10) {
      val id = worker.get_id("test")
      println(id)
    }

    for (i <- 0 until 10) {
      val id = worker.get_id(s"test$i")
      //println(id)
    }
  }

  @Test
  def testGenerateincreasing {
    val worker = new IdWorker(1, 1)
    var lastId = 0L
    for (i <- 1 to 100) {
      val id = worker.nextId
      assertTrue(id  > lastId)
      lastId = id
    }
  }


  @Test
  def testRollOverSequence {
    // put a zero in the low bit so we can detect overflow from the sequence
    val workerId = 4
    val datacenterId = 4
    val worker = new IdWorker(workerId, datacenterId)
    val startSequence = 0xFFFFFF-20
    val endSequence = 0xFFFFFF+20
    worker.sequence = startSequence

    for (i <- startSequence to endSequence) {
      val id = worker.nextId
      assertEquals( ((id & workerMask) >> 12), workerId)
    }
  }


  @Test
  def testProperlyMaskTimestamp {
    val worker = new EasyTimeWorker(31, 31)
    for (i <- 1 to 100) {
      val t = System.currentTimeMillis
      worker.timeMaker = (() => t)
      val id = worker.nextId
      assertEquals( ((id & timestampMask) >> 22), (t - worker.twepoch))
    }
  }


  @Test
  def testProperlyMaskWorker {
    val workerId = 0x1F
    val datacenterId = 0
    val worker = new IdWorker(workerId, datacenterId)
    for (i <- 1 to 1000) {
      val id = worker.nextId
      assertEquals( ((id & workerMask) >> 12), workerId)
    }
  }

}