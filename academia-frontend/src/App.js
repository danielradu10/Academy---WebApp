import React, { use, useEffect, useState } from "react";
import LoginForm from "./components/LoginForm/LoginForm";
import StudentsList from "./components/StudentsList";
import LogoutButton from "./components/Logout";
import UserMode from "./components/UserMode";
import DisciplinesList from "./components/Disciplines";
import InsertProfessor from "./components/InsertProfessor";
import ProfessorsQuery from "./components/ProfessorsQuery";
import UploadMaterialsForm from "./components/UploadMaterials";
import CreateAccount from "./components/CreateAccount";
import InsertStudent from "./components/InsertStudent";
import { fetchDisciplinesForProfessor, fetchDisciplinesForProfessorByLink, fetchStudentsByLink } from "./services/api";
import GetAccounts from "./components/GetAccounts";
import InsertDiscipline from "./components/InsertDiscipline";
import GetDisciplines from "./components/AllDisciplines";
import InsertPonderiForm from "./components/InsertPonderi";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("jwt"));
  const [userMode, setUserMode] = useState(localStorage.getItem("userMode") || null); // Restaurăm modul din localStorage
  const [view, setView] = useState("home");
  const [email, setEmail] = useState(localStorage.getItem("email") || ""); // Restaurăm email-ul din localStorage
  const [studentProfile, setStudentProfile] = useState(null); // Profilul studentului
  const [professorProfile, setProfessorProfile] = useState(null);
  const [disciplineCode, setDisciplineCode] = useState("");
  const [professorDisciplines, setProfessorDisciplines] = useState("");
  const [professorDisciplineLinks, setProfessorDisciplineLinks] = useState("");
  const [professorStudentsLinks, setProfessorStudentsLinks] = useState("");
  const [professorStudents, setProfessorStudents] = useState("");
  const [selectedDisciplineForPonderi, setSelectedDisciplineForPonderi] = useState("");
  // Reconstruim starea utilizatorului după refresh
  useEffect(() => {
    if (isLoggedIn && email && !userMode) {
      // Dacă suntem autentificați, dar nu avem `userMode`, determinăm din nou rolul.
      console.log("Restoring user mode...");
    }
  }, [isLoggedIn, email, userMode]);

  
  const handleLoginSuccess = (userEmail) => {
    setIsLoggedIn(true);
    setEmail(userEmail); // Setăm emailul direct în state
    localStorage.setItem("email", userEmail); // Salvăm email-ul în localStorage
    console.log("Login successful. Email:", userEmail);
  };

  const handleModeChange = (mode) => {
    setUserMode(mode);
    localStorage.setItem("userMode", mode); // Salvăm modul în localStorage
    console.log("Mode set to:", mode);
  };

  const handleStudentProfileLoad = (profile) => {
    setStudentProfile(profile);
    console.log("Student Profile Loaded:", profile);
  };

  const handleDisciplinesPageChange = async (link) =>{
    if (link){
      const data = await fetchDisciplinesForProfessorByLink(link.href);
      console.log(data);
      setProfessorDisciplines(data.disciplines);
      setProfessorDisciplineLinks(data._links);
    }
    console.log(link);

  }

  const handleInsertPonderiClick = (disciplineCode) => {
    setSelectedDisciplineForPonderi(disciplineCode);
  };

  const handleStudentsPageChange = async (link) =>{
    if (link){
      const data = await fetchStudentsByLink(link.href);
      console.log(data);
      setProfessorStudents(data.students);
      setProfessorStudentsLinks(data._links);
    }
    console.log(link);

  }

  const handleProfessorProfileLoad = async (profile) => {
    setProfessorProfile(profile);
    console.log("Professor Profile Loaded:", profile);
  
    try {
      const disciplines = await fetchDisciplinesForProfessorByLink(profile._links.professorDisciplinesLink.href); 
      setProfessorDisciplines(disciplines.disciplines || []); 
      setProfessorDisciplineLinks(disciplines._links)
      console.log("Professor Disciplines Loaded:", disciplines);

      const students = await fetchStudentsByLink(profile._links.professorStudentsLink.href)
      console.log("Professor Students Loaded:", students);
      setProfessorStudents(students.students || []);
      setProfessorStudentsLinks(students._links);
      console.log(professorStudents);
      console.log("Links");
      console.log(students._links);
      console.log("Links2");
      console.log(professorStudentsLinks)
      
    } catch (err) {
      console.error("Failed to load professor disciplines:", err);
    }
  };
  


  

  const handleLogout = () => {
    setIsLoggedIn(false);
    setView("home");
    setUserMode(null);
    setStudentProfile(null);
    setEmail(""); // Resetăm emailul
    localStorage.removeItem("jwt");
    localStorage.removeItem("email");
    localStorage.removeItem("userMode");
  };



  return (
    <div className="App">
      <h1>Welcome to Academia</h1>
      {isLoggedIn ? (
        <>
          {/* Transmitem emailul direct */}
          <UserMode email={email} onModeChange={handleModeChange} onStudentProfileLoad={handleStudentProfileLoad} onProfessorProfileLoad={handleProfessorProfileLoad} />
          {userMode === "admin" ? (
            <>
              <nav>
                <button onClick={() => setView("home")}>Home</button> |{" "}
                <button onClick={() => setView("insertDiscipline")}>Insert discipline</button>
                <button onClick={() => setView("viewAllDisciplines")}>View disciplines</button>

                <button onClick= {() => setView("insertStudent")}>Insert student</button>
                <button onClick={() => setView("students")}>View Students</button>
                <button onClick={() => setView("insertProfessor")}>Insert Professor</button>
                <button onClick={() => setView("viewProfessors")}>View Professors</button>
                <button onClick={() => setView("insertAccount")}>Insert account</button>
                <button onClick={() => setView("getAccounts")}>Get accounts</button>

                <LogoutButton onLogout={handleLogout} />
              </nav>
              <div>
                {view === "home" && <p>Welcome Admin! Select an option from the menu.</p>}
                {view === "insertDiscipline" && <InsertDiscipline />}
                {view === "viewAllDisciplines" && <GetDisciplines />}
                {view === "students" && <StudentsList />}
                {view === "insertProfessor" && <InsertProfessor />}
                {view === "viewProfessors" && <ProfessorsQuery />}
                {view === "insertAccount" && <CreateAccount />} 
                {view === "insertStudent" && <InsertStudent />}
                {view === "getAccounts" && <GetAccounts />}


              </div>
            </>
          ) : userMode === "professor" ? (
            <div>
              <p>You are logged in as a Professor.</p>
              <nav>
                <button onClick={() => setView("home")}>Home</button> |{" "}
                <button onClick={() => setView("uploadLabMaterials")}>Upload Laboratory Materials</button>

                <LogoutButton onLogout={handleLogout} />
              </nav>
              <div>
                {view === "home" && <p>Welcome, Professor! Choose an action.</p>}
                {view === "uploadLabMaterials" && (
                <UploadMaterialsForm disciplines={professorDisciplines} />
              )}


 

              </div>
            {professorProfile ? (
              <div>
              <h2>Professor Profile</h2>
              <p><strong>Name:</strong> {professorProfile.professor.firstName} {professorProfile.professor.lastName}</p>
              <p><strong>Email:</strong> {professorProfile.professor.emailProfessor}</p>

              <h2>My disciplines</h2>
              {professorDisciplines.length === 0 ? (
                <p>No disciplines found for this professor.</p>
              ) : (
                <div>
                  <ul>
                    {professorDisciplines.map((discipline) => (
                      <li key={discipline.discipline.disciplineCode}>
                        {discipline.discipline.disciplineName}
                        <button
                          style={{ marginLeft: "10px" }}
                          onClick={() =>
                            handleInsertPonderiClick(discipline.discipline.disciplineCode)
                          }
                        >
                          Insert Ponderi
                        </button>
                      </li>
                    ))}
                  </ul>
                  <button
                    onClick={() =>
                      handleDisciplinesPageChange(professorDisciplineLinks.previousPage)
                    }
                  >
                    Previous page
                  </button>
                  <button
                    onClick={() =>
                      handleDisciplinesPageChange(professorDisciplineLinks.nextPage)
                    }
                  >
                    Next page
                  </button>
                </div>
              )}

            {selectedDisciplineForPonderi && (
              <div style={{ marginTop: "20px", padding: "10px", border: "1px solid black" }}>
                <h3>Insert Ponderi for {selectedDisciplineForPonderi}</h3>
                <InsertPonderiForm disciplineCode={selectedDisciplineForPonderi} />
                <button
                  style={{ marginTop: "10px" }}
                  onClick={() => setSelectedDisciplineForPonderi(null)}
                >
                  Close
                </button>
              </div>
            )}
              
              <h2>My Students</h2>
              {professorStudents.length === 0 ? (
                  <p> No students found for this professor </p>
              ) : (
                <div>
                <ul>
                  {professorStudents.map((student) => (
                    <li key={student.student.studentId}>
                      {student.student.email}
                    </li>
                  ))}
                </ul>
                <button onClick={() => handleStudentsPageChange(professorStudentsLinks.previousPage)}> Previous page </button>
                <button onClick={() => handleStudentsPageChange(professorStudentsLinks.nextPage)}> Next page </button>
                </div>
              )}
            </div>
            ) : (
              <p>Loading your professor profile...</p>
            )}
              <LogoutButton onLogout={handleLogout} />
            </div>
          ) : userMode === "student" ? (
            <div>
            <p>You are logged in as a Student.</p>
            {studentProfile ? (
              <div>
                <h3>Student Profile</h3>
                <p><strong>Name:</strong> {studentProfile.student.firstName} {studentProfile.student.lastName}</p>
                <p><strong>Email:</strong> {studentProfile.student.email}</p>
                <p><strong>Year:</strong> {studentProfile.student.yearDegree}</p>
                <p><strong>Group:</strong> {studentProfile.student.groupName}</p>
                <p><strong>Study Cycle:</strong> {studentProfile.student.studyCycle}</p>

                <DisciplinesList linkToStudentDisciplines={studentProfile._links.studentDisciplines.href} />
              </div>
            ) : (
              <p>Loading your student profile...</p>
            )}
            <LogoutButton onLogout={handleLogout} />
          </div>
          ) : (
            <p>Determining your role...</p>
          )}
        </>
      ) : (
        <LoginForm onLoginSuccess={handleLoginSuccess} />
      )}
    </div>
  );
}

export default App;
