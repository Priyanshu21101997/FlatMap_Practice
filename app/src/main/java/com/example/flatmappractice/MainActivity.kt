package com.example.flatmappractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.flatmappractice.Model.Post
import com.example.flatmappractice.network.APIService
import com.example.flatmappractice.network.RetroInstance
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers


import retrofit2.Call

class MainActivity : AppCompatActivity() {

    private var apiService:APIService? = null
    private lateinit var postLists: Observable<List<Post>>
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiService = RetroInstance.getRetroClient()!!.create(APIService::class.java)
        postLists = apiService!!.getPosts()


        getObservablePosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
              // ANdroid scehdulers . main thread not coming
            .flatMap {
                return@flatMap getObservableComments(it)
            }
            .subscribe(object : Observer<Post> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }


                // If want to set updated comments in recycler view do it here
                override fun onNext(t: Post) {
                    Log.d("Response", "onNext: {${t.id}")
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            })



    }

    // Gets comments and sets it to comments in posts
    private fun getObservableComments(post:Post):Observable<Post>?{
        return apiService?.getComments(post.id)
            ?.map {
                post.comments = it
                return@map post
            }
            ?.subscribeOn(Schedulers.io())
    }


    private fun getObservablePosts(): Observable<Post>{
       return  postLists
            .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .flatMap {
                return@flatMap Observable.fromIterable(it)
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}