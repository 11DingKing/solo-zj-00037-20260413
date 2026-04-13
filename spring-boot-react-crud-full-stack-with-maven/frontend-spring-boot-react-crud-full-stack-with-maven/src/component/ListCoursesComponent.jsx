import React, { Component } from "react";
import CourseDataService from "../service/CourseDataService";

const INSTRUCTOR = "in28minutes";
const PAGE_SIZE = 10;

class ListCoursesComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      courses: [],
      message: null,
      page: 0,
      size: PAGE_SIZE,
      totalElements: 0,
      totalPages: 0,
      sortField: "id",
      sortDirection: "asc",
    };
    this.deleteCourseClicked = this.deleteCourseClicked.bind(this);
    this.updateCourseClicked = this.updateCourseClicked.bind(this);
    this.addCourseClicked = this.addCourseClicked.bind(this);
    this.refreshCourses = this.refreshCourses.bind(this);
    this.handlePageChange = this.handlePageChange.bind(this);
    this.handleSortChange = this.handleSortChange.bind(this);
  }

  componentDidMount() {
    this.refreshCourses();
  }

  refreshCourses() {
    const sort = `${this.state.sortField},${this.state.sortDirection}`;
    CourseDataService.retrieveAllCourses(
      INSTRUCTOR,
      this.state.page,
      this.state.size,
      sort,
    )
      .then((response) => {
        this.setState({
          courses: response.data.content,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages,
          page: response.data.number,
        });
      })
      .catch((error) => {
        console.error("Error fetching courses:", error);
        if (error.response && error.response.status === 400) {
          this.setState({
            message: error.response.data.message || "Invalid sort parameter",
          });
        }
      });
  }

  deleteCourseClicked(id) {
    CourseDataService.deleteCourse(INSTRUCTOR, id).then((response) => {
      this.setState({ message: `Delete of course ${id} Successful` });
      this.refreshCourses();
    });
  }

  addCourseClicked() {
    this.props.history.push(`/courses/-1`);
  }

  updateCourseClicked(id) {
    console.log("update " + id);
    this.props.history.push(`/courses/${id}`);
  }

  handlePageChange(newPage) {
    if (newPage >= 0 && newPage < this.state.totalPages) {
      this.setState({ page: newPage }, () => this.refreshCourses());
    }
  }

  handleSortChange(field) {
    let newDirection = "asc";
    if (this.state.sortField === field && this.state.sortDirection === "asc") {
      newDirection = "desc";
    }
    this.setState(
      {
        sortField: field,
        sortDirection: newDirection,
        page: 0,
      },
      () => this.refreshCourses(),
    );
  }

  getSortIcon(field) {
    if (this.state.sortField !== field) {
      return "↕";
    }
    return this.state.sortDirection === "asc" ? "↑" : "↓";
  }

  renderPagination() {
    const { page, totalPages, totalElements } = this.state;

    if (totalPages <= 1) {
      return null;
    }

    const pages = [];
    const maxVisiblePages = 5;
    let startPage = Math.max(0, page - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return (
      <nav>
        <ul className="pagination justify-content-center">
          <li className={`page-item ${page === 0 ? "disabled" : ""}`}>
            <button
              className="page-link"
              onClick={() => this.handlePageChange(0)}
              disabled={page === 0}
            >
              First
            </button>
          </li>
          <li className={`page-item ${page === 0 ? "disabled" : ""}`}>
            <button
              className="page-link"
              onClick={() => this.handlePageChange(page - 1)}
              disabled={page === 0}
            >
              Previous
            </button>
          </li>

          {pages.map((p) => (
            <li key={p} className={`page-item ${p === page ? "active" : ""}`}>
              <button
                className="page-link"
                onClick={() => this.handlePageChange(p)}
              >
                {p + 1}
              </button>
            </li>
          ))}

          <li
            className={`page-item ${page === totalPages - 1 ? "disabled" : ""}`}
          >
            <button
              className="page-link"
              onClick={() => this.handlePageChange(page + 1)}
              disabled={page === totalPages - 1}
            >
              Next
            </button>
          </li>
          <li
            className={`page-item ${page === totalPages - 1 ? "disabled" : ""}`}
          >
            <button
              className="page-link"
              onClick={() => this.handlePageChange(totalPages - 1)}
              disabled={page === totalPages - 1}
            >
              Last
            </button>
          </li>
        </ul>
        <div className="text-center text-muted">
          Showing page {page + 1} of {totalPages} ({totalElements} total items)
        </div>
      </nav>
    );
  }

  render() {
    return (
      <div className="container">
        <h3>All Courses</h3>
        {this.state.message && (
          <div className="alert alert-success">{this.state.message}</div>
        )}
        <div className="container">
          <table className="table">
            <thead>
              <tr>
                <th
                  style={{ cursor: "pointer" }}
                  onClick={() => this.handleSortChange("id")}
                >
                  Id {this.getSortIcon("id")}
                </th>
                <th
                  style={{ cursor: "pointer" }}
                  onClick={() => this.handleSortChange("description")}
                >
                  Description {this.getSortIcon("description")}
                </th>
                <th>Update</th>
                <th>Delete</th>
              </tr>
            </thead>
            <tbody>
              {this.state.courses.map((course) => (
                <tr key={course.id}>
                  <td>{course.id}</td>
                  <td>{course.description}</td>
                  <td>
                    <button
                      className="btn btn-success"
                      onClick={() => this.updateCourseClicked(course.id)}
                    >
                      Update
                    </button>
                  </td>
                  <td>
                    <button
                      className="btn btn-warning"
                      onClick={() => this.deleteCourseClicked(course.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {this.renderPagination()}

          <div className="row">
            <button className="btn btn-success" onClick={this.addCourseClicked}>
              Add
            </button>
          </div>
        </div>
      </div>
    );
  }
}

export default ListCoursesComponent;
