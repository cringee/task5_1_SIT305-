package com.example.task51

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.parcelize.Parcelize

// Define a data class for a news article that implements Parcelable
@Parcelize
data class NewsArticle(
    val title: String,
    val description: String,
    val imageUrl: String
) : Parcelable

class MainActivity : AppCompatActivity() {

    private lateinit var topStoriesRecyclerView: RecyclerView

    // Set up the main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the RecyclerView for the top stories
        topStoriesRecyclerView = findViewById(R.id.top_stories_recycler_view)
        topStoriesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val topStories = listOf(
            NewsArticle("Top Story 1", "Description 1", "https://mackayhearing.com.au/wp-content/uploads/2016/06/News-Article-1-Newspaper-extract.png"),
            NewsArticle("Top Story 2", "Description 2", "https://static.independent.co.uk/2021/09/06/17/Screen%20Shot%202021-09-06%20at%2012.36.39%20PM.png"),
            NewsArticle("Top Story 3", "Description 3", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9JMgXWBGiKsUNfuV7b_3K_HBB2BfXh-Ov3Q&usqp=CAU")
        )
        val adapter = NewsAdapter(topStories)
        topStoriesRecyclerView.adapter = adapter

        // Add news fragment to container
        val newsFragment = NewsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.news_fragment_container, newsFragment)
            .commit()

    }

}

class NewsFragment : Fragment() {
    // Declare views and variables
    private lateinit var newsTitleTextView: TextView
    private lateinit var relatedNewsRecyclerView: RecyclerView
    private lateinit var myFragmentLayout: LinearLayout
    private lateinit var newsArticles: List<NewsArticle>

    // Inflate the fragment layout and initialize views and variables
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        // Initialize views
        newsTitleTextView = view.findViewById(R.id.news_title_text_view)
        relatedNewsRecyclerView = view.findViewById(R.id.related_news_recycler_view)
        relatedNewsRecyclerView.layoutManager = LinearLayoutManager(context)

        myFragmentLayout = view.findViewById(R.id.myFragmentLayout)
        //myFragmentLayout.setOnClickListener(this)
        // Initialize news articles
        newsArticles = listOf(
            NewsArticle("News 1", "Description 1", "https://i.pinimg.com/originals/1f/8e/ba/1f8ebaac5a0b6b9217c65b33c8aec1c1.jpg"),
            NewsArticle("News 2", "Description 2", "https://i.pinimg.com/originals/ac/b4/5f/acb45f3972eea8d18980231593913900.jpg"),
            NewsArticle("News 3", "Description 3", "https://i2.wp.com/101planners.com/borders/wp-content/uploads/2019/09/newspaper-generator-7-600x464.jpeg")
        )
        // Initialize news articles
        val adapter = NewsAdapter(newsArticles)
        relatedNewsRecyclerView.adapter = adapter

        return view
    }

}

class NewsAdapter(private val newsArticles: List<NewsArticle>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    // Define the ViewHolder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.news_title_text_view)
        val descriptionTextView: TextView = itemView.findViewById(R.id.news_description_text_view)
        val imageView: ImageView = itemView.findViewById(R.id.news_image_view)
    }
    // Inflate the news_item layout and return a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }
    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsArticle = newsArticles[position]
        holder.titleTextView.text = newsArticle.title
        holder.descriptionTextView.text = newsArticle.description
        Glide.with(holder.itemView.context)
            .load(newsArticle.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imageView)
        // Set up click listener to open DetailsActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            if (holder.itemView.context is MainActivity) {
                intent.putExtra("newsArticle", newsArticles[position])
                intent.setClass(holder.itemView.context, DetailsActivity::class.java)
                intent.putExtra("newsArticles", ArrayList(newsArticles))
                holder.itemView.context.startActivity(intent)
            }
        }
    }
    // Return the number of news articles
    override fun getItemCount(): Int {
        return newsArticles.size
    }
}



class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Get the news article and related news articles from the intent
        val newsArticle = intent.getParcelableExtra<NewsArticle>("newsArticle")
        val relatedNewsArticles = intent.getParcelableArrayListExtra<NewsArticle>("newsArticles")
        Log.d("DetailActivity", "News article: $relatedNewsArticles")

        // Create a list of news articles and a RelatedNewsFragment with a random related news article
        val relatedNewsFragment = relatedNewsArticles?.shuffled()?.firstOrNull()?.let { RelatedNewsFragment.newInstance(it) }

        // Replace the related news fragment container with the RelatedNewsFragment
        if (relatedNewsFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.related_news_fragment_container, relatedNewsFragment)
                ?.commit()
        }
        // Initialize views and set their values
        val newsTitleTextView = findViewById<TextView>(R.id.news_title_text_view)
        val newsDescriptionTextView = findViewById<TextView>(R.id.news_description_text_view)
        val newsImageView = findViewById<ImageView>(R.id.news_image_view)

        newsTitleTextView.text = newsArticle?.title
        newsDescriptionTextView.text = newsArticle?.description
        Glide.with(this)
            .load(newsArticle?.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(newsImageView)
    }
}

class RelatedNewsFragment : Fragment() {

    companion object {
        const val ARG_RELATED_NEWS_ARTICLE = "related_news_article"
        // Create a new instance of the fragment with the given related news article
        fun newInstance(relatedNewsArticle: NewsArticle): Fragment {
            val args = Bundle().apply {
                putParcelable(ARG_RELATED_NEWS_ARTICLE, relatedNewsArticle)
            }
            return RelatedNewsFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var newsTitleTextView: TextView
    private lateinit var newsDescriptionTextView: TextView
    private lateinit var newsImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the fragment_related_news layout
        val view = inflater.inflate(R.layout.fragment_related_news, container, false)

        // Initialize views
        newsTitleTextView = view.findViewById(R.id.news_title_text_view)
        newsDescriptionTextView = view.findViewById(R.id.news_description_text_view)
        newsImageView = view.findViewById(R.id.news_image_view)

        // Get the related news article from the arguments
        val relatedNewsArticle = arguments?.getParcelable<NewsArticle>(ARG_RELATED_NEWS_ARTICLE)
        // Set the views' values
        newsTitleTextView.text = "Related " + relatedNewsArticle?.title
        newsDescriptionTextView.text = relatedNewsArticle?.description
        Glide.with(this)
            .load(relatedNewsArticle?.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(newsImageView)

        return view
    }
}

