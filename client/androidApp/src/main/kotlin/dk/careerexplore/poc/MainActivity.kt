package dk.careerexplore.poc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dk.careerexplore.poc.data.AndroidKeyValueStore

class MainActivity : ComponentActivity(), PlatformActions {
    // Kept as a field until printing has started; a WebView that is garbage-collected cancels the job.
    private var printWebView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val container = AppContainer(AndroidKeyValueStore(applicationContext), baseUrl = BuildConfig.API_BASE_URL)
        setContent {
            App(container, platform = this) { enabled, onBack -> BackHandler(enabled, onBack) }
        }
    }

    override fun shareText(subject: String, text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(send, subject))
    }

    override fun printHtml(jobName: String, html: String) {
        val webView = WebView(this)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                val printManager = getSystemService(PRINT_SERVICE) as PrintManager
                printManager.print(jobName, view.createPrintDocumentAdapter(jobName), PrintAttributes.Builder().build())
                printWebView = null
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        printWebView = webView
    }
}
