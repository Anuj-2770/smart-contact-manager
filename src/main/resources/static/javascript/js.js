console.log("this is the console page of java script");

const toggleSideBar = () => {
    const sidebar = document.querySelector(".sidebar");

    if (sidebar.classList.contains("visible")) {
        sidebar.classList.remove("visible");
    } else {
        sidebar.classList.add("visible");
    }
};

