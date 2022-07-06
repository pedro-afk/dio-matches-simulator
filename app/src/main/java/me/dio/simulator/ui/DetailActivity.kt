package me.dio.simulator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import me.dio.simulator.databinding.ActivityDetailBinding
import me.dio.simulator.domain.Match

class DetailActivity : AppCompatActivity() {
    object Extras {
        const val MATCH = "EXTRA_MATCH"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadMatchFromExtra()
    }

    private fun loadMatchFromExtra() {
        intent?.extras?.getParcelable<Match>(Extras.MATCH)?.let { // let = se tudo der certo retorna o que precisamos
            Glide.with(this).load(it.place.image).into(binding.ivPlace)

            Glide.with(this).load(it.homeTeam.image).circleCrop().into(binding.ivHomeTeam)
            binding.tvHomeTeamName.text = it.homeTeam.name
            binding.tvHomeTeamScore.text = it.homeTeam.score.toString()
            binding.rbAwayTeamStars.rating = it.awayteam.stars.toFloat()

            Glide.with(this).load(it.awayteam.image).circleCrop().into(binding.ivAwayTeam)
            binding.tvAwayTeamName.text = it.awayteam.name
            binding.tvAwayTeamScore.text = it.awayteam.score.toString()
            binding.rbHomeTeamStars.rating = it.homeTeam.stars.toFloat()

            supportActionBar?.title = it.place.name
            binding.tvDescription.text = it.description
        }
    }
}