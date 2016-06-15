package com.github.jarimatti.configurationutil

import java.io.FileNotFoundException
import java.net.URL
import java.util

import org.junit.runner.RunWith
import org.osgi.framework.Bundle
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.mockito.Mockito._

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class ConfigurationUtilSpec extends FlatSpec with Matchers with MockitoSugar with BeforeAndAfter {

  behavior of "ConfigurationUtil"

  val filename = "test-reference.cnf"

  def urls = {
    val v = new util.Vector[URL]
    v.add(this.getClass.getClassLoader.getResource(filename))
    v.elements()
  }

  it should "use settings from reference configuration if user has not overridden them" in {
    val bundle = mock[Bundle]
    when(bundle.findEntries("/", filename, false)).thenReturn(urls)

    val config = ConfigurationUtil.load(
      Map.empty[String, AnyRef].asJava,
      filename,
      bundle).asScala

    config.size shouldBe 2
    config should contain ("key1" -> "hello")
    config should contain ("key2" -> "world")
  }

  it should "let user override reference configuration settings" in {
    val bundle = mock[Bundle]
    when(bundle.findEntries("/", filename, false)).thenReturn(urls)

    val userConfig = Map[String, AnyRef]("key1" -> "foo")

    val config = ConfigurationUtil.load(
      userConfig.asJava,
      filename,
      bundle).asScala

    config.size shouldBe 2
    config should contain ("key1" -> "foo")
    config should contain ("key2" -> "world")
  }

  it should "let user define settings not in reference config" in {
    val bundle = mock[Bundle]
    when(bundle.findEntries("/", filename, false)).thenReturn(urls)

    val userConfig = Map[String, AnyRef]("key3" -> "bar")

    val config = ConfigurationUtil.load(
      userConfig.asJava,
      filename,
      bundle).asScala

    config.size shouldBe 3
    config should contain ("key1" -> "hello")
    config should contain ("key2" -> "world")
    config should contain ("key3" -> "bar")
  }

  it should "throw an IOException if the reference configuration is not found" in {
    val bundle = mock[Bundle]
    val notfound = "non-existing-config.cnf"
    when(bundle.findEntries("/", notfound, false)).thenReturn(new util.Vector[URL].elements)

    an[FileNotFoundException] should be thrownBy {
      ConfigurationUtil.load(
        Map.empty[String, AnyRef].asJava,
        notfound,
        bundle)
    }
  }

  it should "use only user settings if the reference configuration is empty" in {
    val bundle = mock[Bundle]
    val empty = "empty-reference.cnf"
    when(bundle.findEntries("/", empty, false)).thenReturn({
      val v = new util.Vector[URL]
      v.add(this.getClass.getClassLoader.getResource(empty))
      v.elements
    })

    val userConfig = Map[String, AnyRef]("key3" -> "bar")

    val config = ConfigurationUtil.load(
      userConfig.asJava,
      empty,
      bundle).asScala

    config.size shouldBe 1
    config should contain ("key3" -> "bar")

  }
}
