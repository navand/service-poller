import Services from "./views/Services";

const dashboardRoutes = [
  {
    path: "services",
    name: "Service List",
    icon: "fa-cogs",
    component: Services,
    layout: "/",
  },
];

export default dashboardRoutes;
