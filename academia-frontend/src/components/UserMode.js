import { useEffect, useState } from "react";
import { fetchStudentByEmail, fetchProfessorByEmail } from "../services/api"; 

const UserMode = ({ email, onModeChange, onStudentProfileLoad, onProfessorProfileLoad }) => {
  const [userMode, setUserMode] = useState("admin");

  useEffect(() => {
    let isMounted = true;

    if (email) {
      const domain = email.split("@")[1];

      const determineModeAndFetch = async () => {
        if (domain.includes("student")) {
          setUserMode("student");
          onModeChange("student");

          try {
            const profile = await fetchStudentByEmail(email);
            console.log("Student Profile:", profile);
            if (isMounted) {
              onStudentProfileLoad(profile);
            }
          } catch (error) {
            console.error("Failed to fetch student profile:", error);
          }
        } else if (domain.includes("academic")) {
          setUserMode("professor");
          onModeChange("professor");

          try {
            const profile = await fetchProfessorByEmail(email);
            console.log("Professor Profile:", profile);
            if (isMounted) {
              onProfessorProfileLoad(profile);
            }
          } catch (error) {
            console.error("Failed to fetch professor profile:", error);
          }

        } else {
          setUserMode("admin");
          onModeChange("admin");
        }
      };

      determineModeAndFetch();
    }

    return () => {
      isMounted = false;
    };
  }, [email]); 

  return null;
};

export default UserMode;
