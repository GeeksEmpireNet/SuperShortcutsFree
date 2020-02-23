package net.geekstools.supershortcuts.PRO.normal

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class AppShortcutsMediatedActivity : Activity() {
    
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        intent?.let {
            val packageName = it.getStringExtra("PackageName")!!

            val openIntent = Intent().apply {
                this.setPackage(packageName)
                if (it.hasExtra("ClassName")) {
                    this.setClassName(packageName, it.getStringExtra("ClassName")!!)
                }
                this.action = Intent.ACTION_MAIN
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.addCategory(Intent.CATEGORY_DEFAULT)
            }
            startActivity(openIntent)
        }

        this@AppShortcutsMediatedActivity.finish()
    }
}