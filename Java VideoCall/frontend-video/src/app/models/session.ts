export interface Session {
  id: number;
  title: string;
  roomName: string;
  startTime: string;
  endTime: string;
  status: 'SCHEDULED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  logopedId: number;
  logopedName: string;
  patientIds: number[];
  canJoin: boolean;
}
