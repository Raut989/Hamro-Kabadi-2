package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SecurityUtils
import com.example.data.repository.KabadiRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Hamro Kabadi", appName)
  }

  @Test
  fun `password hashing generates deterministic sha256`() {
    val hash1 = SecurityUtils.hashPassword("password123")
    val hash2 = SecurityUtils.hashPassword("password123")
    val hash3 = SecurityUtils.hashPassword("differentPassword")

    assertEquals(hash1, hash2)
    assertNotEquals(hash1, hash3)
    assertEquals(64, hash1.length)
  }

  @Test
  fun `distance calculation is accurate for Kathmandu valley points`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = KabadiRepository(context)
    // Distance between Baneshwor (27.6934, 85.3412) and Patan (27.6744, 85.3235) is ~2.7 - 2.9 KM
    val dist = repo.calculateDistanceKm(27.6934, 85.3412, 27.6744, 85.3235)
    assertTrue("Distance $dist should be between 2.0 and 4.0 km", dist in 2.0..4.0)
  }
}
