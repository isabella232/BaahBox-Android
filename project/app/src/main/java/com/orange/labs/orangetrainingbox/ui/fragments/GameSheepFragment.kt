/**
    Orange Baah Box
    Copyright (C) 2017 – 2019 Orange SA

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.orange.labs.orangetrainingbox.ui.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.orange.labs.orangetrainingbox.R
import com.orange.labs.orangetrainingbox.btle.TrainingBoxViewModel
import com.orange.labs.orangetrainingbox.tools.properties.SheepGameConfiguration
import com.orange.labs.orangetrainingbox.tools.properties.readSheepAdditionalConfiguration
import com.orange.labs.orangetrainingbox.tools.properties.readSheepGameConfiguration
import com.orange.labs.orangetrainingbox.ui.animations.IconAnimator
import kotlinx.android.synthetic.main.fragment_game_star_intro.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.find
import android.util.TypedValue
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.marginTop
import com.orange.labs.orangetrainingbox.tools.logs.Logger
import org.jetbrains.anko.margin
import android.util.DisplayMetrics





// *******
// Classes
// *******

// TODO Enrich this doc
/**
 * A simple [Fragment] subclass.
 * Use the [GameSheepFragment.newInstance] factory method to create an instance of this fragment.
 *
 * @author Marc Poppleton
 * @author Pierre-Yves Lapersonne
 * @since 23/10/2018
 * @version 2.0.0
 * @see [AbstractGameFragment]
 */
class GameSheepFragment : AbstractGameFragment() {


    // **********
    // Properties
    // **********

    /**
     * Permits to play some kind of animations for the game icon
     */
    private var gameIconAnimator: IconAnimator? = null


    // ***********************************
    // Inherited from AbstractGameFragment
    // ***********************************

    /**
     * Identifier of the layout introducing the game
     */
    override val introductionLayout: Int
        get() = R.layout.fragment_game_sheep_intro

    /**
     * Identifier of the layout with the game itself, where the user can play
     */
    override val playingLayout: Int
        get() = R.layout.fragment_game_sheep_playing

    /**
     * Identifier fo the restart / outro layout with e.g. the final score, a button to restart...
     */
    override val restartLayout: Int
        get() = R.layout.fragment_game_sheep_outro

    /**
     * Identifier of the game's theming color
     */
    override val themingColor: Int
        get() = R.color.pink

    /**
     * Identifier of the game's title
     */
    override val screenTitle: Int
        get() = R.string.title_game_sheep

    /**
     * An action define in the navigation graph to use to go from the introduction screen to the
     * playing screen.
     */
    override val actionFromIntroductionToPlaying: Int
        get() = R.id.action_gameSheepFragment_to_gameSheepPlayingFragment

    /**
     * An action to trigger to go back to the playing screen, e.g. for restart of the game
     */
    override val actionGoToPlaying: Int
        get() = R.id.action_global_gameSheepPlayingFragment

    /**
     * Loads from the SafeArgs the argument "playing"
     */
    override fun loadFromArgsFlagPlaying(): Boolean {
        return GameSheepFragmentArgs.fromBundle(arguments).playing
    }

    /**
     * Loads from the SafeArgs the argument "introducing"
     */
    override fun loadFromArgsFlagIntroducing(): Boolean {
        return GameSheepFragmentArgs.fromBundle(arguments).introducing
    }

    /**
     * Uses an [IconAnimator] to display several images in the gameicon widget
     */
    override fun startIntroductionAnimation() {
        gameIconAnimator = IconAnimator()
        val period = context!!.readSheepAdditionalConfiguration().sheepAnimationPeriod
        gameIconAnimator!!.animateGameIcon((activity as AppCompatActivity), gameIcon, period,
            arrayOf(R.mipmap.ic_sheep_moving_1, R.mipmap.ic_sheep_moving_2))
    }

    /**
     * Stops the [IconAnimator]
     */
    override fun stopIntroductionAnimation() {
        gameIconAnimator!!.stopAnimateGameIcon()
    }


    // *******
    // Methods
    // *******

    /**
     * Fragment lifecycle
     */
    override fun onPause() {
        super.onPause()
        gameIconAnimator?.stopAnimateGameIcon()
    }

    // TODO Enrich this doc
    /**
     * Prepare the Bluetooth LE sensor observer.
     * Defines the behaviour of the observer using the sensor value, and thus the logic of the game.
     * Get from properties the thresholds of the sheep game.
     * Use the value returned by the sensor and process the game logic.
     */
    override fun prepareSensorObserver() {

        // Get the ViewModel.
        model = activity?.run {
            ViewModelProviders.of(this).get(TrainingBoxViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val gameConfiguration = context!!.readSheepGameConfiguration()
        val difficultyFactor = getDifficultyNumericValue()

        // Define the observer
        val sensorBObserver = Observer<Int> { sensorValue ->

            processBaahBoxData(gameConfiguration, sensorValue, difficultyFactor)

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.sensorB.observe(this, sensorBObserver)

    }

    /**
     * Prepares the inheriting classes for the game logic, the animations and other logic for the game.
     * The game animations start here.
     */
    override fun prepareGameLayout() {
        startIntroductionAnimation()
        moveFences()
        // TODO Check if collisions
        // TODO Display final animation (collision or all fences jumped)
    }

    
    // **********
    // Game logic
    // **********


    // TODO Enrich this doc
    /**
     * The logic of this game.
     * Get from properties the thresholds to apply for the game.
     * According to the gotten sensor value, will do things (display score, congratulation message, ...)
     *
     * @param configuration The game configuration
     * @param userInput The data given by the Baah box
     * @param difficultyFactor The numeric value to apply for inputs to create a kind of difficulty
     */
    private fun processBaahBoxData(configuration: SheepGameConfiguration, userInput: Int,
                                   difficultyFactor: Double) {

        // TODO

    }

    /**
     * Makes the fences images moves above the floor from the right to the left
     */
    private fun moveFences() {

        val loadFenceImageView = {
            val toDp: (Float) -> Int = {
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, it, resources.displayMetrics).toInt()
            }
            val view = ImageView(this@GameSheepFragment.context)
            view.id = View.generateViewId()
            view.imageResource = R.mipmap.ic_sheep_fence
            val layoutParams = ConstraintLayout.LayoutParams(toDp(100f), toDp(100f))
            layoutParams.setMargins(toDp(0f), toDp(214f), toDp(0f), toDp(0f))
            view.layoutParams = layoutParams
            view
        }

        val addFenceImageView: (ImageView) -> Unit = {
            val parentLayout = find<ConstraintLayout>(R.id.clGameSheep)
            parentLayout.addView(it)
            val constraintSet = ConstraintSet()
            constraintSet.clone(parentLayout)
            constraintSet.connect(it.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(it.id, ConstraintSet.END, R.id.gameSheepFloor, ConstraintSet.END)
            constraintSet.applyTo(parentLayout)
        }

        val moveFenceImageView: (ImageView) -> Unit = {
            //val parentLayout = find<ConstraintLayout>(R.id.clGameSheep)
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

            val objectAnimator = ObjectAnimator.ofFloat(it, "translationX", 200f,metrics.widthPixels* (-1f))
            objectAnimator.duration = 2000
            objectAnimator.repeatCount = 10
            objectAnimator.repeatMode = ValueAnimator.RESTART
            objectAnimator.addListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(animation: Animator) {
                    Logger.d(">>>>>>> Start")

                }

                override fun onAnimationEnd(animation: Animator) {
                    Logger.d(">>>>>>> End")
                    val parentLayout = find<ConstraintLayout>(R.id.clGameSheep)
                    parentLayout.removeView(it)
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {
                    Logger.d(">>>>>>> Repeat")
                }
            })

            objectAnimator.start()


            /*
            val animSlide = AnimationUtils.loadAnimation(this@GameSheepFragment.context, R.anim.fence_slide)
            animSlide.repeatCount = 6
            animSlide.repeatMode = Animation.INFINITE
            animSlide.duration = 5000
            animSlide.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    Logger.d(">>>> Start of animation")
                }
                override fun onAnimationEnd(animation: Animation) {
                    Logger.d(">>>> End of animation")

                    //val parentLayout = find<ConstraintLayout>(R.id.clGameSheep)
                    //parentLayout.removeView(it)

                }
                override fun onAnimationRepeat(animation: Animation) {
                    Logger.d(">>>> Repeat of animation")
                }
            })
            it.startAnimation(animSlide)
            */

        }

        // Repeat for each fence according to settings
        // TODO

        // Load fence asset
        val fenceView = loadFenceImageView()

        // Add widget to the view
        addFenceImageView(fenceView)

        // Make the asset move from right to left
        moveFenceImageView(fenceView)

    }

}
