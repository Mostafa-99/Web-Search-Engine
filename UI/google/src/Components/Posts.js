import React from 'react';

const Posts = ({ posts, loading }) => {
  if (loading) {
    return <h2>Loading...</h2>;
  }

  return (
    <ul className='list-group mb-4'>
      {posts.map((post,num) => (
        <li key={post.id} className='list-group-item'>
          <div class="card">
            <h5 class="card-header">{num+1}</h5>
            <div class="card-body">
              <h5 class="card-title">{post.name}</h5>
              <p class="card-text mt-3">With supporting text below as a natural lead-in to additional content.</p>
              <p class="card-text">With supporting text below as a natural lead-in to additional content.</p>
              <a href="#" class="btn btn-primary">Go somewhere</a>
            </div>
          </div>
        </li>
      ))}
    </ul>
  );
};

export default Posts;