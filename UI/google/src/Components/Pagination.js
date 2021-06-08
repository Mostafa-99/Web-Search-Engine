import React from 'react';

const Pagination = ({ postsPerPage, totalPosts }) => {
  const pageNumbers = [];

  for (let i = 1; i <= Math.ceil(totalPosts / postsPerPage); i++) {
    pageNumbers.push(i);
  }
  function paginate ( pageNumber){
    localStorage.setItem('currentPage',pageNumber);
}
  return (
    <nav>
      <ul className='pagination flex-wrap'>
        {pageNumbers.map(number => (
          <li key={number} className='page-item' style={{width:"50px"}}>
            <a onClick={() => paginate(number)} href='' className='page-link'>
              {number}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
};

export default Pagination;