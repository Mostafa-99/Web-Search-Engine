import React from 'react';

const Posts = ({ posts, loading }) => {
  if (loading) {
    return <h2>Loading...</h2>;
  }

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
                <h5 class="card-title">{post.Title}</h5>
                <p class="card-text">{post.description}</p>
                <p class="card-text mt-3">Your Searched word appeared {post.TF} times on this website.</p>
                <a href={post.URL} class="btn btn-primary">{post.URL}</a>
              </div>
            </div>
          </li>
        ))
      }
    </ul>
  );
};

export default Posts;