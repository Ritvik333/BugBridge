import Navbar from "../components/Navbar";
import SideNav from "../components/SideNav";
import BugListWithFilters from "../components/MyDraftWithFilter";

export default function MyDraftPage() {
  return (
    <div className="flex flex-col h-screen">
      <Navbar />
      <div className="flex flex-1">
        <SideNav />
        <div className="flex-1 bg-gray-100 p-4 overflow-auto">
          <BugListWithFilters showAddButton={false} /> {/* Hide the Plus button */}
        </div>
      </div>
    </div>
  );
}
