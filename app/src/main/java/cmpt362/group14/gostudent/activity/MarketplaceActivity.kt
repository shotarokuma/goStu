package cmpt362.group14.gostudent.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cmpt362.group14.gostudent.R
import cmpt362.group14.gostudent.databinding.ActivityMarketplaceBinding

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMarketplaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}