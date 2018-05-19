package com.example.multidotview

/**
 * Created by anweshmishra on 19/05/18.
 */

import android.content.*
import android.graphics.*
import android.view.View
import android.view.MotionEvent

class MultiDotView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : MDLRenderer = MDLRenderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class MultiDot(var i : Int, val state : State = State()) {

        fun draw(canvas  : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val l : Float = Math.min(w, h) * 0.4f
            val r : Float = Math.min(w, h) * 0.04f
            val rx : Float = Math.min(w, h) * 0.1f
            paint.color = Color.WHITE
            canvas.save()
            canvas.translate(w/2, h/2)
            for (i in 0..1) {
                canvas.save()
                canvas.rotate(i * 90f * state.scales[3])
                for (k in 0..1) {
                    canvas.save()
                    canvas.translate(l * (1 - 2 * k) * state.scales[4], 0f)
                    for (j in 0..2) {
                        canvas.save()
                        canvas.translate(rx * (i - 1), 0f)
                        canvas.drawCircle(0f, 0f, r * state.scales[j], paint)
                        canvas.restore()
                    }
                    canvas.restore()
                }
                canvas.restore()
            }
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class MDLRenderer(var view : MultiDotView) {

        private val animator : Animator = Animator(view)

        private val mdl : MultiDot = MultiDot(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            mdl.draw(canvas, paint)
            animator.animate {
                mdl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            mdl.startUpdating {
                animator.start()
            }
        }
    }
}