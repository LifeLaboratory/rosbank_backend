from app.api.base import base_name as names
from app.api.base.base_sql import Sql


def get_id_user_by_profile(args):
    query = """
    select id_user from "users" where id_profile = {id_profile}
            """
    return Sql.exec(query=query, args=args)
