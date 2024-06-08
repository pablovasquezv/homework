import { Rol } from './Rol';

export class ProfileFilter {
    user: string;
    // roles: Array<Rol>;

    constructor(
        user: string,
        // roles: Array<Rol>,
    ) {
        this.user = user;
        //this.roles = roles;
    }
}