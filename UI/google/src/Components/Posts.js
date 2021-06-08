import React from 'react';
import "./Posts.css"

const Posts = ({ posts, loading }) => {
  // if (loading) {
  //   return <h2>Loading...</h2>;
  // }

  return (
    <ul className='list-group mb-4'>
      {
      posts.length == 0?
        ""
      :
        posts.map((post,num) => (
          <li key={post.id} className='list-group-item'>
            <div class="card">
              <h5 class="card-header"></h5>
              <div class="card-body">
                <a href={post.URL} class="card-title-link">{post.URL}</a>
                <a href={post.URL} ><h5 class="card-title">{post.Title}</h5></a>
                <p class="card-text">{post.description}</p>
                <p id="search-number" class="card-text mt-3"><i>Your searched word appeared {post.TF} times on this website.</i></p>
              </div>
            </div>
          </li>
        ))
      }
    </ul>
  );
};

export default Posts;