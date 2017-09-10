package com.luseen.ribble.presentation.screen.shot_detail


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.luseen.ribble.R
import com.luseen.ribble.domain.entity.Comment
import com.luseen.ribble.domain.entity.Shot
import com.luseen.ribble.presentation.adapter.CommentRecyclerAdapter
import com.luseen.ribble.presentation.base_mvp.base.BaseFragment
import com.luseen.ribble.presentation.widget.navigation_view.NavigationId
import com.luseen.ribble.utils.getExtra
import com.luseen.ribble.utils.glide.TransformationType
import com.luseen.ribble.utils.glide.load
import com.luseen.ribble.utils.takeColor
import kotlinx.android.synthetic.main.fragment_shot_detail.*
import javax.inject.Inject

class ShotDetailFragment : BaseFragment<ShotDetailContract.View, ShotDetailContract.Presenter>(), ShotDetailContract.View {

    companion object {
        const val SHOT_EXTRA_KEY = "shot_extra_key"

        fun getBundle(shot: Shot): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(SHOT_EXTRA_KEY, shot)
            return bundle
        }
    }

    @Inject
    protected lateinit var shotDetailPresenter: ShotDetailPresenter

    private var recyclerAdapter: CommentRecyclerAdapter? = null
    private lateinit var shot: Shot

    override fun injectDependencies() {
        activityComponent.inject(this)
    }

    override fun layoutResId() = R.layout.fragment_shot_detail

    override fun initPresenter() = shotDetailPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        shot = this getExtra SHOT_EXTRA_KEY
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        with(shot) {
            shotDetailImage.load(image.normal)
            shotAuthor.text = user.name
            authorLocation.text = user.location
            authorImage.load(user.avatarUrl, TransformationType.CIRCLE)
        }
        progressBar.backgroundCircleColor = takeColor(R.color.colorPrimary)

        //TODO move to attributes
        likeLayout.layoutText = shot.likesCount
        likeLayout.imageResId = R.drawable.heart_full
        likeLayout.imageTint = R.color.colorPrimary
        viewCountLayout.layoutText = shot.viewsCount
        viewCountLayout.imageResId = R.drawable.eye
        viewCountLayout.imageTint = R.color.cyan
        bucketLayout.layoutText = shot.bucketCount
        bucketLayout.imageResId = R.drawable.bucket
        bucketLayout.imageTint = R.color.blue_gray
    }

    override fun onDataReceive(commentList: List<Comment>) {
        updateAdapter(commentList)
    }

    override fun getShotId(): String? {
        return shot.id
    }

    override fun showNoComments() {
        noCommentsText.setAnimatedText(getString(R.string.no_comments_text))
    }

    override fun showLoading() {
        progressBar.start()
    }

    override fun hideLoading() {
        progressBar.stop()
    }

    override fun showError(message: String?) {
        //TODO("not implemented")
    }

    private fun updateAdapter(commentList: List<Comment>) {
        recyclerAdapter?.update(commentList) ?: this setUpRecyclerView commentList
    }

    private infix fun setUpRecyclerView(commentList: List<Comment>) {
        recyclerAdapter = CommentRecyclerAdapter(commentList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = recyclerAdapter
    }

    override fun getTitle(): String {
        return NavigationId.SHOT_DETAIL.name //TODO
    }
}
