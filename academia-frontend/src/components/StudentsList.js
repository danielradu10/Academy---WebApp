import React, { useEffect, useState } from "react";
import { fetchStudentsByQuery, fetchStudentById, fetchStudents, fetchStudentsByLink } from "../services/api";
import DisciplinesList from "./Disciplines";
import PatchDisciplines from "./PatchDisciplines";

const StudentsList = () => {
  const [students, setStudents] = useState([]);
  const [error, setError] = useState(null); 
  const [loading, setLoading] = useState(true); 
  const [selectedStudent, setSelectedStudent] = useState(null); 
  const [showDisciplines, setShowDisciplines] = useState(null);
  const [showPatchDisciplines, setShowPatchDisciplines] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchStudents();
        console.log(data);
        setStudents(data); 
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false); 
      }
    };

    fetchData();
  }, []);

  const [searchParams, setSearchParams] = useState({
    lastName: "",
    groupName: "",
    yearDegree: "",
    study: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setSearchParams((prevParams) => ({
      ...prevParams,
      [name]: value,
    }));
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await fetchStudentsByQuery(searchParams);
      console.log("DATA")
      console.log(data)
      setStudents(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };


  const handleViewStudent = async (id) => {
    try {
      const data = await fetchStudentById(id); 
      console.log(data);
      setSelectedStudent(data); 
    } catch (err) {
      console.error("Failed to fetch student details:", err);
    }
  };

  const handleNextPage = async(link) => {
    console.log(link)
    try{
      const data = await fetchStudentsByLink(link);
      console.log(data);
      setStudents(data);
    } catch (err){
      console.error("Failed to fetch students:", err);

    }
  }

  const handlePreviousPage = async(link) => {
    console.log(link)
    try{
      const data = await fetchStudentsByLink(link);
      console.log(data);
      setStudents(data);
    } catch (err){
      console.error("Failed to fetch students:", err);

    }
  }


  


  if (loading) {
    return <p>Loading students...</p>; 
  }

  if (error) {
    return <p>Error fetching students: {error}</p>; 
  }

  return (
    <div>
      <h2>Students List</h2>

      <form onSubmit={handleSearch}>
        <input
          type="text"
          name="lastName"
          placeholder="Last Name"
          value={searchParams.lastName}
          onChange={handleInputChange}
        />
        <input
          type="text"
          name="groupName"
          placeholder="Group Name"
          value={searchParams.groupName}
          onChange={handleInputChange}
        />
        <input
          type="number"
          name="yearDegree"
          placeholder="Year Degree"
          value={searchParams.yearDegree}
          onChange={handleInputChange}
        />
        <input
          type="text"
          name="study"
          placeholder="Study Cycle"
          value={searchParams.study}
          onChange={handleInputChange}
        />
        <button type="submit">Search</button>
      </form>

      <ul>
        {students.students.map((student) => (
          <li key={student.student.studentId}>
            {student.student.firstName} {student.student.lastName} - {student.student.email}{" "}
            <button onClick={() => handleViewStudent(student.student.studentId)}>
              View Details
            </button>
          </li>
        ))}
      </ul>
      <button onClick={() => handlePreviousPage(students._links.previousPage.href)}>
        Previous page
      </button>

      <button onClick={() => handleNextPage(students._links.nextPage.href)}>
        Next page
      </button>

      {selectedStudent && (
        <div style={{ marginTop: "20px", padding: "10px", border: "1px solid black" }}>
          <h3>Student Details</h3>
          <p><strong>ID:</strong> {selectedStudent.student.studentId}</p>
          <p><strong>Name:</strong> {selectedStudent.student.firstName} {selectedStudent.student.lastName}</p>
          <p><strong>Email:</strong> {selectedStudent.student.email}</p>
          <p><strong>Grupa:</strong> {selectedStudent.student.groupName}</p>
          <p><strong>Anul:</strong> {selectedStudent.student.yearDegree}</p>
          <p><strong>Studiaza la:</strong> {selectedStudent.student.studyCycle}</p>
          <p><strong>Message:</strong> {selectedStudent.message}</p>
          <button onClick={() => setShowDisciplines(!showDisciplines)}>
            {showDisciplines ? "Hide Disciplines" : "View Disciplines"}
          </button>

          <button onClick={() => setShowPatchDisciplines(!showPatchDisciplines)}>
            {showPatchDisciplines ? "Hide Patch Disciplines" : "Patch Disciplines"}
          </button>

          {showDisciplines && (
            <DisciplinesList linkToStudentDisciplines={selectedStudent._links.studentDisciplines.href} />
          )}

          
          {showPatchDisciplines && (
            <PatchDisciplines studentId={selectedStudent.student.studentId} />
          )}
        </div>
      )}
    </div>
  );
};

export default StudentsList;
